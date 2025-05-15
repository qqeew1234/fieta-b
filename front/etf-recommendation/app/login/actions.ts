"use server"

import { cookies }  from "next/headers"
import { redirect } from "next/navigation"

export async function login(loginId: string, password: string) {
    const res = await fetch("http://localhost:8080/api/v1/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ loginId, password }),
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
    }

    const data = await res.json()
    const cookieStore = await cookies()
    cookieStore.set({ name: "accessToken", value: data.token, httpOnly: true, path: "/" })
    cookieStore.set("login_id", loginId, { path: "/" })

    redirect("/")               // 성공 시 리다이렉트
}
