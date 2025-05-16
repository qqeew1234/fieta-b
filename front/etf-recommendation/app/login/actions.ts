export async function login(loginId: string, password: string) {
    const res = await fetch("https://localhost:8443/api/v1/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ loginId, password, role: "USER" }),
        credentials: "include"
    })

    if (!res.ok) {
        let message = "로그인 실패"
        try {
            const errData = await res.json()
            if (errData?.message) message = errData.message
        } catch {
            message = await res.text()
        }
        throw new Error(message)
    }    // 성공 시 리다이렉트
}