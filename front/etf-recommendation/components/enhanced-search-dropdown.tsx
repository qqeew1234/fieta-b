"use client"

import type React from "react"
import { useState, useEffect, useRef, type KeyboardEvent } from "react"
import { Search, Clock, ArrowUp, ArrowDown } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import type { ETF } from "@/components/EtfCard"
import { useRouter } from "next/navigation"

// 로컬 스토리지 키
const RECENT_SEARCHES_KEY = "recent-etf-searches"

// 최대 최근 검색어 수
const MAX_RECENT_SEARCHES = 5

interface SearchDropdownProps {
    items: ETF[]
    onSelect: (item: ETF) => void
    placeholder?: string
    showRecent?: boolean
    redirectToDetail?: boolean
}

export default function EnhancedSearchDropdown({
                                                   items,
                                                   onSelect,
                                                   placeholder = "검색어를 입력하세요",
                                                   showRecent = false,
                                                   redirectToDetail = true,
                                               }: SearchDropdownProps) {
    const [query, setQuery] = useState("")
    const [isOpen, setIsOpen] = useState(false)
    const [selectedIndex, setSelectedIndex] = useState(-1)
    const [recentSearches, setRecentSearches] = useState<ETF[]>([])
    const dropdownRef = useRef<HTMLDivElement>(null)
    const inputRef = useRef<HTMLInputElement>(null)
    const router = useRouter()

    // 최근 검색어 로드
    useEffect(() => {
        if (showRecent) {
            try {
                const saved = localStorage.getItem(RECENT_SEARCHES_KEY)
                if (saved) {
                    const parsed = JSON.parse(saved)
                    setRecentSearches(parsed)
                }
            } catch (error) {
                console.error("최근 검색어 로드 실패:", error)
            }
        }
    }, [showRecent])

    // 최근 검색어 저장
    const saveToRecentSearches = (item: ETF) => {
        if (!showRecent) return

        try {
            const updatedRecent = [item, ...recentSearches.filter((i) => i.id !== item.id)].slice(0, MAX_RECENT_SEARCHES)
            setRecentSearches(updatedRecent)
            localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(updatedRecent))
        } catch (error) {
            console.error("최근 검색어 저장 실패:", error)
        }
    }

    // 검색 결과 필터링
    const filteredItems = query.trim()
        ? items.filter(
            (item) =>
                item.name.toLowerCase().includes(query.toLowerCase()) ||
                item.ticker.toLowerCase().includes(query.toLowerCase()),
        )
        : []

    // 검색어 하이라이트 처리
    const highlightMatch = (text: string) => {
        if (!query.trim()) return text

        const regex = new RegExp(`(${query})`, "gi")
        const parts = text.split(regex)

        return parts.map((part, i) => {
            if (part.toLowerCase() === query.toLowerCase()) {
                return (
                    <span key={i} className="bg-amber-700 text-white font-medium">
            {part}
          </span>
                )
            }
            return part
        })
    }

    // 항목 선택 처리
    const handleSelectItem = (item: ETF) => {
        onSelect(item)
        saveToRecentSearches(item)
        setQuery("")
        setIsOpen(false)
        setSelectedIndex(-1)

        // 상세 페이지로 리다이렉트
        if (redirectToDetail) {
            router.push(`/etf/${item.id}`)
        }
    }

    // 키보드 네비게이션
    const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
        const items = query.trim() ? filteredItems : recentSearches

        if (!isOpen || items.length === 0) return

        // 아래 화살표
        if (e.key === "ArrowDown") {
            e.preventDefault()
            setSelectedIndex((prev) => (prev < items.length - 1 ? prev + 1 : 0))
        }
        // 위 화살표
        else if (e.key === "ArrowUp") {
            e.preventDefault()
            setSelectedIndex((prev) => (prev > 0 ? prev - 1 : items.length - 1))
        }
        // 엔터
        else if (e.key === "Enter" && selectedIndex >= 0) {
            e.preventDefault()
            handleSelectItem(items[selectedIndex])
        }
        // ESC
        else if (e.key === "Escape") {
            e.preventDefault()
            setIsOpen(false)
        }
    }

    // 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false)
            }
        }

        document.addEventListener("mousedown", handleClickOutside)
        return () => {
            document.removeEventListener("mousedown", handleClickOutside)
        }
    }, [])

    // 최근 검색어 삭제
    const removeRecentSearch = (e: React.MouseEvent, itemId: string) => {
        e.stopPropagation()
        const updatedRecent = recentSearches.filter((item) => item.id !== itemId)
        setRecentSearches(updatedRecent)
        localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(updatedRecent))
    }

    return (
        <div className="relative" ref={dropdownRef}>
            <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                <Input
                    ref={inputRef}
                    placeholder={placeholder}
                    className="pl-10 pr-10 bg-gray-800 border-gray-700 text-white placeholder:text-gray-400 focus-visible:ring-gray-600"
                    value={query}
                    onChange={(e) => {
                        setQuery(e.target.value)
                        setIsOpen(true)
                        setSelectedIndex(-1)
                    }}
                    onFocus={() => setIsOpen(true)}
                    onKeyDown={handleKeyDown}
                />
                {query && (
                    <Button
                        variant="ghost"
                        size="icon"
                        className="absolute right-2 top-1/2 transform -translate-y-1/2 h-6 w-6 text-gray-400 hover:text-white hover:bg-gray-700"
                        onClick={() => {
                            setQuery("")
                            inputRef.current?.focus()
                        }}
                    >
                        <span className="sr-only">지우기</span>
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        >
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </Button>
                )}
            </div>

            {isOpen && (
                <div className="absolute z-50 mt-1 w-full bg-gray-800 rounded-md shadow-lg border border-gray-700 max-h-[350px] overflow-y-auto">
                    {/* 검색 결과 */}
                    {query.trim() && (
                        <>
                            {filteredItems.length > 0 ? (
                                <div className="py-2">
                                    <div className="px-3 py-1.5 text-xs font-medium text-gray-400">검색 결과</div>
                                    {filteredItems.map((item, index) => (
                                        <div
                                            key={item.id}
                                            className={`px-3 py-2 cursor-pointer flex justify-between items-center hover:bg-gray-700 ${
                                                index === selectedIndex ? "bg-gray-700" : ""
                                            }`}
                                            onClick={() => handleSelectItem(item)}
                                        >
                                            <div>
                                                <div className="font-medium text-white">{highlightMatch(item.name)}</div>
                                                <div className="text-sm text-gray-400 flex items-center gap-2">
                                                    <span>{highlightMatch(item.ticker)}</span>
                                                    <Badge variant="outline" className="text-xs border-gray-600 text-gray-300">
                                                        {item.theme}
                                                    </Badge>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <div className={`font-medium ${item.change >= 0 ? "text-green-500" : "text-red-500"}`}>
                                                    {item.change >= 0 ? "+" : ""}
                                                    {item.change}%
                                                </div>
                                                <div className="text-sm text-gray-400">{item.price.toLocaleString()}원</div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="px-3 py-4 text-center text-gray-400">검색 결과가 없습니다</div>
                            )}
                        </>
                    )}

                    {/* 최근 검색어 */}
                    {showRecent && !query.trim() && recentSearches.length > 0 && (
                        <div className="py-2">
                            <div className="px-3 py-1.5 text-xs font-medium text-gray-400 flex items-center">
                                <Clock className="h-3 w-3 mr-1" />
                                최근 검색
                            </div>
                            {recentSearches.map((item, index) => (
                                <div
                                    key={item.id}
                                    className={`px-3 py-2 cursor-pointer flex justify-between items-center hover:bg-gray-700 ${
                                        index === selectedIndex ? "bg-gray-700" : ""
                                    }`}
                                    onClick={() => handleSelectItem(item)}
                                >
                                    <div>
                                        <div className="font-medium text-white">{item.name}</div>
                                        <div className="text-sm text-gray-400 flex items-center gap-2">
                                            <span>{item.ticker}</span>
                                            <Badge variant="outline" className="text-xs border-gray-600 text-gray-300">
                                                {item.theme}
                                            </Badge>
                                        </div>
                                    </div>
                                    <div className="flex items-center">
                                        <div className="text-right mr-2">
                                            <div className={`font-medium ${item.change >= 0 ? "text-green-500" : "text-red-500"}`}>
                                                {item.change >= 0 ? "+" : ""}
                                                {item.change}%
                                            </div>
                                            <div className="text-sm text-gray-400">{item.price.toLocaleString()}원</div>
                                        </div>
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            className="h-6 w-6 opacity-50 hover:opacity-100 text-gray-400 hover:text-white hover:bg-gray-700"
                                            onClick={(e) => removeRecentSearch(e, item.id)}
                                        >
                                            <span className="sr-only">삭제</span>
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                width="14"
                                                height="14"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                strokeWidth="2"
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                            >
                                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                                <line x1="6" y1="6" x2="18" y2="18"></line>
                                            </svg>
                                        </Button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* 키보드 네비게이션 도움말 */}
                    {(filteredItems.length > 0 || (showRecent && recentSearches.length > 0)) && (
                        <div className="px-3 py-2 border-t border-gray-700 text-xs text-gray-500 flex items-center justify-center gap-3">
                            <div className="flex items-center">
                                <ArrowUp className="h-3 w-3 mr-1" /> <ArrowDown className="h-3 w-3 mr-1" /> 이동
                            </div>
                            <div>Enter 선택</div>
                            <div>Esc 닫기</div>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}
