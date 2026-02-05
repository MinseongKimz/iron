"use client";

import React from 'react';

export default function StatsPage() {
    // Placeholder Data
    const weeklyData = [
        { day: '월', volume: 4500 },
        { day: '화', volume: 3200 },
        { day: '수', volume: 0 },
        { day: '목', volume: 5100 },
        { day: '금', volume: 2800 },
        { day: '토', volume: 6000 },
        { day: '일', volume: 0 },
    ];
    const maxVolume = Math.max(...weeklyData.map(d => d.volume)) || 1;

    return (
        <div className="flex flex-col h-[calc(100vh-80px)] overflow-y-auto">
            <header className="p-4 border-b border-gray-800">
                <h1 className="font-bold text-center">성장 분석 (Beta)</h1>
            </header>

            <div className="p-4 space-y-6">

                {/* Weekly Volume Chart */}
                <div className="card">
                    <h3 className="font-bold mb-4 text-sm text-secondary">주간 총 볼륨 (kg)</h3>
                    <div className="flex items-end justify-between h-40 gap-2">
                        {weeklyData.map((d, i) => (
                            <div key={i} className="flex flex-col items-center flex-1">
                                <div className="relative w-full flex justify-center items-end h-full">
                                    <div
                                        className={`w-full max-w-[20px] rounded-t ${d.volume > 0 ? 'bg-primary' : 'bg-gray-800'}`}
                                        style={{ height: `${(d.volume / maxVolume) * 100}%` }}
                                    ></div>
                                </div>
                                <span className="text-xs text-gray-500 mt-2">{d.day}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* 1RM Trend (Placeholder) */}
                <div className="card">
                    <div className="flex justify-between items-center mb-4">
                        <h3 className="font-bold text-sm text-secondary">종목별 1RM 추이</h3>
                        <select className="bg-black text-xs border border-gray-700 rounded px-2 py-1">
                            <option>벤치프레스</option>
                            <option>스쿼트</option>
                            <option>데드리프트</option>
                        </select>
                    </div>

                    <div className="h-32 bg-black/30 rounded flex items-center justify-center text-gray-500 text-sm border border-gray-800 border-dashed">
                        데이터가 쌓이면 그래프가 표시됩니다.
                    </div>
                </div>

                {/* Muscle Balance */}
                <div className="card">
                    <h3 className="font-bold text-sm text-secondary mb-4">부위별 비중 (Top 3)</h3>
                    <div className="space-y-3">
                        <div className="flex items-center text-sm">
                            <span className="w-12 text-gray-400">가슴</span>
                            <div className="flex-1 h-2 bg-gray-800 rounded-full mx-2">
                                <div className="h-full bg-blue-400 rounded-full" style={{ width: '60%' }}></div>
                            </div>
                            <span className="w-8 text-right">60%</span>
                        </div>
                        <div className="flex items-center text-sm">
                            <span className="w-12 text-gray-400">등</span>
                            <div className="flex-1 h-2 bg-gray-800 rounded-full mx-2">
                                <div className="h-full bg-purple-400 rounded-full" style={{ width: '30%' }}></div>
                            </div>
                            <span className="w-8 text-right">30%</span>
                        </div>
                        <div className="flex items-center text-sm">
                            <span className="w-12 text-gray-400">하체</span>
                            <div className="flex-1 h-2 bg-gray-800 rounded-full mx-2">
                                <div className="h-full bg-red-400 rounded-full" style={{ width: '10%' }}></div>
                            </div>
                            <span className="w-8 text-right">10%</span>
                        </div>
                    </div>

                    <div className="mt-4 p-2 bg-red-500/10 border border-red-500/50 rounded text-xs text-red-200">
                        ⚠️ 하체 비중이 너무 낮습니다! 이번 주는 하체 어떠세요?
                    </div>
                </div>

            </div>
        </div>
    );
}
