"use client"

import { useState, useEffect } from "react";
import Link from "next/link"
import { ChevronRight, Home, TrendingUp, User, LogIn, UserPlus, Star, Menu, X, Newspaper } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { useMobile } from "@/hooks/use-mobile"

interface SidebarClientProps {
  loginId: string
}

// 테마 카테고리 데이터
const themeCategories = [
  { id: "tech", name: "기술" },
  { id: "finance", name: "금융" },
  { id: "healthcare", name: "헬스케어" },
  { id: "consumer", name: "소비재" },
  { id: "energy", name: "에너지" },
  { id: "global", name: "글로벌" },
]

export default function SidebarClient({ loginId }: SidebarClientProps) {
  const [showThemes, setShowThemes] = useState(false)
  const [isOpen,    setIsOpen]    = useState(false)
  const isMobile                  = useMobile()
  const toggleSidebar = () => {
    setIsOpen(!isOpen)
  }

  return (
    <>
      {isMobile && (
        <Button variant="ghost" size="icon" className="fixed top-4 left-4 z-50" onClick={toggleSidebar}>
          {isOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
        </Button>
      )}

      <div
        className={cn(
          "bg-slate-900 text-white w-64 min-h-screen flex flex-col transition-all duration-300 ease-in-out",
          isMobile && (isOpen ? "fixed left-0 top-0 z-40" : "fixed -left-64 top-0 z-40"),
        )}
      >
        <div className="p-4 border-b border-slate-700">
          <h1 className="text-xl font-bold">폭삭 벌었수다</h1>
          <p className="text-sm text-slate-400">ETF 추천 서비스</p>
        </div>

        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            <li>
              <Link href="/" className="flex items-center gap-2 p-2 rounded-md hover:bg-slate-800">
                <Home className="h-5 w-5" />
                <span>홈</span>
              </Link>
            </li>

            <li>
              <div
                className="flex items-center justify-between p-2 rounded-md hover:bg-slate-800 cursor-pointer"
                onClick={() => setShowThemes(!showThemes)}
              >
                <div className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5" />
                  <span>테마별 ETF</span>
                </div>
                <ChevronRight className={cn("h-4 w-4 transition-transform", showThemes && "rotate-90")} />
              </div>

              {showThemes && (
                <ul className="ml-6 mt-2 space-y-1 border-l-2 border-slate-700 pl-2">
                  {themeCategories.map((theme) => (
                    <li key={theme.id}>
                      <Link href={`/themes/${theme.id}`} className="block p-2 rounded-md hover:bg-slate-800">
                        {theme.name}
                      </Link>
                    </li>
                  ))}
                </ul>
              )}
            </li>

            <li>
              <Link href="/recommendations" className="flex items-center gap-2 p-2 rounded-md hover:bg-slate-800">
                <Star className="h-5 w-5" />
                <span>추천받기</span>
              </Link>
            </li>

            <li>
              <Link href="/news" className="flex items-center gap-2 p-2 rounded-md hover:bg-slate-800">
                <Newspaper className="h-5 w-5" />
                <span>경제 뉴스</span>
              </Link>
            </li>

            <li>
              <Link href={`/profile/${loginId}`} className="flex items-center gap-2 p-2 rounded-md hover:bg-slate-800">
                <User className="h-5 w-5" />
                <span>내 프로필</span>
              </Link>
            </li>
          </ul>
        </nav>

        <div className="p-4 border-t border-slate-700">
          <div className="flex gap-2">
            <Button variant="outline" className="flex-1 bg-white text-slate-900 hover:bg-slate-100" asChild>
              <Link href="/login">
                <LogIn className="h-4 w-4 mr-2" />
                로그인
              </Link>
            </Button>
            <Button className="flex-1 bg-green-600 hover:bg-green-700" asChild>
              <Link href="/register">
                <UserPlus className="h-4 w-4 mr-2" />
                가입
              </Link>
            </Button>
          </div>
        </div>
      </div>
    </>
  )
}
