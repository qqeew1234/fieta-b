package EtfRecommendService.comment.serviece;

import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.comment.domain.CommentLike;
import EtfRecommendService.comment.dto.*;
import EtfRecommendService.comment.exception.NoExistsEtfIdException;
import EtfRecommendService.comment.exception.NoExistsUserIdException;
import EtfRecommendService.comment.repository.CommentLikeRepository;
import EtfRecommendService.comment.repository.qdto.CommentAndLikesCountQDto;
import EtfRecommendService.comment.repository.qdto.SortedCommentsQDto;
import EtfRecommendService.comment.repository.CommentRepository;
import EtfRecommendService.comment.repository.CommentRepositoryCustom;
import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final AdminRepository adminRepository;

    public CommentsPageList readAll(Pageable pageable, Long etfId) {
        Sort sort = pageable.getSort();
        String sortOrderName = sort.get().findFirst().map(Sort.Order::getProperty).orElse("createdAt");

        if (sortOrderName.equals("likes")) {
            SortedCommentsQDto qDto = commentRepositoryCustom.findAllByEtfIdOrderByLikes(pageable, etfId);
            List<CommentAndLikesCountQDto> commentAndLikesCountQDtoPage = qDto.commentAndLikesCountQDtoPage();

            List<CommentResponse> commentResponseList = commentAndLikesCountQDtoPage.stream()
                    .map(c -> {
                        return CommentResponse.builder()
                                .id(c.comment().getId())
                                .userId(c.comment().getUser().getId())
                                .nickName(c.comment().getUser().getNickName())
                                .content(c.comment().getContent())
                                .likesCount(c.likesCount())
                                .createdAt(c.comment().getCreatedAt())
                                .build();
                    }).toList();

            return CommentsPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(qDto.totalCount())
                    .totalPages((int) Math.ceil((double) qDto.totalCount() / pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        } else {
            Page<Comment> commentPage = commentRepository.findAllByEtfId(etfId, pageable);
            List<Comment> commentList = commentPage.getContent();
            List<CommentResponse> commentResponseList = commentList.stream().map(
                            c -> CommentResponse
                                    .builder()
                                    .id(c.getId())
                                    .userId(c.getUser().getId())
                                    .content(c.getContent())
                                    .createdAt(c.getCreatedAt())
                                    .build()
                    )
                    .toList();
            return CommentsPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(commentPage.getTotalElements())
                    .totalPages((int) Math.ceil((double) commentPage.getTotalElements() / pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        }
    }

    //Comment Create
    @Transactional
    public void create(String loginId, CommentCreateRequest commentCreateRequest) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new NoExistsUserIdException("User ID not found"));
        Etf etf = etfRepository.findById(commentCreateRequest.etfId())
                .orElseThrow(() -> new NoExistsEtfIdException("Etf Id not found"));


        // 1) 같은 ETF에 동일한 내용의 마지막 댓글이 있다면 차단
        commentRepository
                .findTopByUserAndEtfOrderByCreatedAtDesc(user, etf)
                .ifPresent(last -> {
                    if (last.getContent().equals(commentCreateRequest.content())) {
                        throw new IllegalArgumentException("똑같은 댓글은 다시 작성할 수 없습니다.");
                    }
                });

        // 2) 어떤 ETF든 사용자가 마지막으로 작성한 댓글과의 시간 차가 5초 미만이면 차단
        commentRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .ifPresent(last -> {
                    Duration diff = Duration.between(last.getCreatedAt(), LocalDateTime.now());
                    if (diff.getSeconds() < 5) {
                        throw new IllegalArgumentException("한 번 작성 후 최소 5초 뒤에 다시 작성 가능합니다.");
                    }
                });


        Comment comment = Comment.builder()
                .content(commentCreateRequest.content())
                .etf(etf)
                .user(user)
                .build();
        comment.addEtfAndUser(etf, user);
// 조건 통과 시 저장
        commentRepository.save(comment);
    }

    //Comment Update

    @Transactional
    public void update(String loginId, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        // 1) 로그인된 유저 조회
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new IllegalArgumentException("User ID not found"));

        // 2) 수정 대상 댓글 로드
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment ID not found"));

        // 3) 권한 검사: 작성자와 일치해야
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        comment.setContent(commentUpdateRequest.content());

        commentRepository.flush();
        System.out.println("▶ updatedAt = " + comment.getUpdatedAt());
    }


    //Comment Soft Delete
    @Transactional
    public void delete(String loginId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        comment.setDeleted(true); // isDeleted = true 로 표시

        // sysout 으로 확인
        System.out.println(">> [Soft Delete] comment.id=" + comment.getId()
                + ", isDeleted=" + comment.isDeleted());


    }

    //좋아요 토글
    @Transactional
    public ToggleLikeResponse toggleLike(String loginId, Long commentId) {
        // 댓글 & 유저 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 기존 좋아요 여부 확인
        Optional<CommentLike> existing = commentLikeRepository
                .findByUser_LoginIdAndComment_Id(loginId, commentId);

        boolean liked;
        if (existing.isPresent()) {
            // 좋아요 취소
            commentLikeRepository.delete(existing.get());
            liked = false;
        } else {
            // 새 좋아요 저장
            CommentLike saved = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(saved);
            liked = true;
        }

        // 최신 좋아요 개수 집계
        Long count = commentLikeRepository.countByComment_Id(commentId);

        // DTO로 묶어서 반환
        return new ToggleLikeResponse(commentId, liked, count);
    }

    public CommentResponse readOneComment(String loginId, Long commentId) {
        //이부분은 Spring Security 적용해서 권한 검증하는 방식으로 교체할것- 임시 권한 검증책
        Admin admin = adminRepository.findByLoginId(loginId).orElseThrow(()->new EntityNotFoundException("User is not Admin"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new EntityNotFoundException("Not found Comment"));
        return CommentResponse.toDto(comment);
    }
}