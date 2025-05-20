#!/bin/bash
# Python 가상환경 설정 및 스크립트 실행

set -e  # 오류 발생 시 스크립트 중단

# 로그 함수 정의
log_info() {
    echo -e "\033[0;32m[INFO]\033[0m $1"
}

log_error() {
    echo -e "\033[0;31m[ERROR]\033[0m $1" >&2
}

log_warning() {
    echo -e "\033[0;33m[WARNING]\033[0m $1"
}

# 기본 설정값
VENV_DIR=".venv"
REQUIREMENTS_FILE="requirements.txt"
PYTHON_SCRIPT=""

# 도움말 메시지
show_help() {
    echo "사용법: $(basename "$0") [옵션]"
    echo ""
    echo "옵션:"
    echo "  -e, --env ENV_NAME      가상환경 디렉토리 이름 (기본값: venv)"
    echo "  -r, --requirements FILE 요구사항 파일 경로 (기본값: requirements.txt)"
    echo "  -f, --file FILE         실행할 파이썬 파일"
    echo "  -h, --help              도움말 표시"
    echo ""
    echo "예시:"
    echo "  $(basename "$0") -f app.py"
    echo "  $(basename "$0") -e custom_env -r custom_requirements.txt -f app.py"
}

# 명령행 인자 처리
while [[ $# -gt 0 ]]; do
    case "$1" in
        -e|--env)
            VENV_DIR="$2"
            shift 2
            ;;
        -r|--requirements)
            REQUIREMENTS_FILE="$2"
            shift 2
            ;;
        -f|--file)
            PYTHON_SCRIPT="$2"
            shift 2
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            log_error "알 수 없는 옵션: $1"
            show_help
            exit 1
            ;;
    esac
done

# 필수 인자 검증
if [ -z "$PYTHON_SCRIPT" ]; then
    log_error "실행할 파이썬 파일을 지정해야 합니다. (-f 옵션 사용)"
    show_help
    exit 1
fi

# Python 버전 확인
if ! command -v python3 &> /dev/null; then
    log_error "Python 3가 설치되어 있지 않습니다."
    exit 1
fi

log_info "Python 버전: $(python3 --version)"

# 1. 가상 환경 생성 및 활성화
if [ ! -d "$VENV_DIR" ]; then
    log_info "가상 환경 생성 중: $VENV_DIR"
    python3 -m venv "$VENV_DIR"
    if [ $? -ne 0 ]; then
        log_error "가상 환경 생성 실패"
        exit 1
    fi
else
    log_info "기존 가상 환경 사용: $VENV_DIR"
fi

# 운영체제에 따라 활성화 스크립트 경로 설정
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows (Git Bash 또는 기타 Unix-like 쉘)
    ACTIVATE_SCRIPT="$VENV_DIR/Scripts/activate"
else
    # Linux, macOS, 기타 Unix-like
    ACTIVATE_SCRIPT="$VENV_DIR/bin/activate"
fi

if [ ! -f "$ACTIVATE_SCRIPT" ]; then
    log_error "활성화 스크립트를 찾을 수 없습니다: $ACTIVATE_SCRIPT"
    exit 1
fi

log_info "가상 환경 활성화 중"
source "$ACTIVATE_SCRIPT"

# 2. 필요한 모듈 설치
if [ -f "$REQUIREMENTS_FILE" ]; then
    log_info "필요한 패키지 설치 중 (from $REQUIREMENTS_FILE)"
    pip install -r "$REQUIREMENTS_FILE"
    if [ $? -ne 0 ]; then
        log_error "패키지 설치 실패"
        exit 1
    fi
else
    log_warning "요구사항 파일을 찾을 수 없습니다: $REQUIREMENTS_FILE. 패키지 설치를 건너뜁니다."
fi

# 3. 파이썬 파일 실행
if [ -f "$PYTHON_SCRIPT" ]; then
    log_info "파이썬 스크립트 실행 중: $PYTHON_SCRIPT"
    python "$PYTHON_SCRIPT"
    EXIT_CODE=$?

    if [ $EXIT_CODE -ne 0 ]; then
        log_error "파이썬 스크립트 실행 실패 (종료 코드: $EXIT_CODE)"
        exit $EXIT_CODE
    fi

    log_info "스크립트 실행 완료"
else
    log_error "파이썬 파일을 찾을 수 없습니다: $PYTHON_SCRIPT"
    exit 1
fi

# 가상 환경 비활성화
deactivate
log_info "가상 환경 비활성화 완료"