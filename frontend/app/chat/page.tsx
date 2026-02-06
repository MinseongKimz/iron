"use client";

import React, { useState } from 'react';
import WorkoutCard from '../components/WorkoutCard';
import { useAuth } from '../context/AuthContext';
import LogoutButton from '../components/LogoutButton';

type Message = {
    role: 'user' | 'ai';
    content: string;
    data?: any; // Parsed workout data
};

import { getApiBaseUrl } from '../utils/apiUtils';

export default function ChatPage() {
    const [input, setInput] = useState('');
    const [messages, setMessages] = useState<Message[]>([
        { role: 'ai', content: '안녕하세요! 오늘 수행한 운동을 자유롭게 적어주세요.\\n 예) \"벤치 60 10회 5셋\"' }
    ]);

    const { user } = useAuth();

    const handleSend = async () => {
        if (!input.trim() || !user) return; // Guard clause for user

        const userMsg: Message = { role: 'user', content: input };
        setMessages(prev => [...prev, userMsg]);
        setInput('');

        // Get selected date (use Korea timezone for default)
        const dateInput = document.getElementById('workout-date') as HTMLInputElement;
        const selectedDate = dateInput?.value || (() => {
            const now = new Date();
            const koreaTime = new Date(now.getTime() + (9 * 60 * 60 * 1000)); // UTC+9
            return koreaTime.toISOString().split('T')[0];
        })();

        try {
            // Use /api/ai/chat as it is the controller we updated
            const response = await fetch(`${getApiBaseUrl()}/api/ai/chat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: user.userId, // use real user id
                    rawInput: input,
                    date: selectedDate
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to fetch');
            }

            const resData = await response.json();
            // resData = { parsedJson: "...", feedback: "..." }

            let parsedExercises = null;
            try {
                if (resData.parsedJson) {
                    const innerData = JSON.parse(resData.parsedJson);
                    parsedExercises = innerData.exercises;
                }
            } catch (e) {
                console.error("JSON Parse error:", e);
            }

            const aiMsg: Message = {
                role: 'ai',
                content: resData.feedback || "운동이 기록되었습니다.",
                data: parsedExercises ? {
                    exercises: parsedExercises
                } : undefined
            };
            setMessages(prev => [...prev, aiMsg]);

        } catch (error) {
            console.error(error);
            setMessages(prev => [...prev, { role: 'ai', content: "죄송합니다. 오류가 발생했습니다. 잠시 후 다시 시도해주세요." }]);
        }
    };

    return (
        <div className="flex flex-col h-screen bg-background text-foreground relative overflow-hidden">
            {/* Header - Glassmorphism */}
            <header className="absolute top-0 w-full z-10 flex justify-center bg-background-80 backdrop-blur-md border-b">
                <div className="w-full max-w-[600px] px-4 py-3 flex justify-between items-center">
                    <div className="flex items-center gap-2">
                        <div className="w-2 h-2 rounded-full bg-primary animate-pulse"></div>
                        <h1 className="text-lg font-bold tracking-tight">Iron AI Coach</h1>
                        {user?.currentStreak && (
                            <div className="flex items-center gap-1 ml-3 px-2 py-1 bg-surface-hover rounded-full border border-border/50">
                                <span className="text-sm">🔥</span>
                                <span className="text-xs font-medium text-foreground">{user.currentStreak}일</span>
                            </div>
                        )}
                    </div>
                    <div className="flex items-center gap-3">
                        <input
                            type="date"
                            id="workout-date"
                            className="bg-surface text-sm text-foreground border rounded-md px-3 py-1.5 focus:outline-none focus:ring-1 transition-all"
                            defaultValue={new Date().toISOString().split('T')[0]}
                        />
                        <LogoutButton />
                    </div>
                </div>
            </header>

            {/* Chat Area */}
            <div className="flex-1 overflow-y-auto scroll-smooth pt-20 pb-40">
                <div className="container min-h-0 pb-4">
                    <div className="space-y-6">
                        {messages.map((msg, idx) => (
                            <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} animate-fadeIn`}>
                                <div className={`message-bubble ${msg.role === 'user' ? 'message-user' : 'message-ai'}`}>
                                    <p className="whitespace-pre-wrap">{msg.content}</p>
                                    {/* Workout Card Data Visualization */}
                                    {msg.data && (
                                        <div className="mt-4 pt-3 border-t" style={{ borderColor: 'rgba(255,255,255,0.1)' }}>
                                            <WorkoutCard summary={msg.data.summary} exercises={msg.data.exercises} />
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Input Area - Fixed Bottom Centered */}
            <div className="bottom-input-container">
                <div className="bottom-input-wrapper">
                    <div className="flex gap-3 relative">
                        <textarea
                            className="flex-1 bg-surface text-foreground border rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-primary focus:ring-1 resize-none h-12 leading-normal transition-all shadow-sm"
                            placeholder="오늘 루틴을 알려주세요... (예: 벤치 60kg 5세트)"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && !e.shiftKey) {
                                    e.preventDefault();
                                    handleSend();
                                }
                            }}
                            style={{ height: '52px' }}
                        />
                        <button
                            onClick={handleSend}
                            disabled={!input.trim()}
                            className="bg-primary text-black font-bold rounded-xl px-5 hover:opacity-90 active:scale-95 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-md"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-5 h-5">
                                <path d="M3.478 2.405a.75.75 0 00-.926.94l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.405z" />
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
