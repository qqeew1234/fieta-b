    import ProfileClient from './profile-client';
import { cookies } from 'next/headers';
import {redirect} from "next/navigation";

export default async function ProfilePage({
                                              params,
                                          }: {
    params: Promise<{ loginId: string }>
}) {
    // 라우트 파라미터에서 loginId 추출
    const { loginId } = await params;

    // 서버에서 쿠키 가져오기
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('accessToken')?.value;
    if (!loginId || !accessToken) {
        redirect('/login');
    }
    // 서버에서 초기 프로필 데이터 가져오기
    let initialProfileData = null;
    if (accessToken) {
        try {
            const response = await fetch(`https://localhost:8443/api/v1/users/${loginId}`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                },
            });

            if (response.ok) {
                initialProfileData = await response.json();
            } else {
                console.error(`서버 오류: ${response.status}`);
            }
        } catch (error) {
            console.error("서버에서 프로필 데이터 로드 오류:", error);
        }
    }

    // 클라이언트 컴포넌트에 데이터 전달
    return <ProfileClient initialProfileData={initialProfileData} loginId={loginId} />;
}