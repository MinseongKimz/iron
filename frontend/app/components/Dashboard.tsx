"use client";

import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getApiBaseUrl } from '../utils/apiUtils';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';

interface VolumeData {
    category: string;
    totalVolume: number;
}

interface FrequencyData {
    category: string;
    count: number;
}

export default function Dashboard() {
    const { user } = useAuth();
    const [volumeData, setVolumeData] = useState<VolumeData[]>([]);
    const [frequencyData, setFrequencyData] = useState<FrequencyData[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;

        const fetchData = async () => {
            try {
                const res = await fetch(`${getApiBaseUrl()}/api/stats/dashboard?userId=${user.userId}`);
                if (res.ok) {
                    const json = await res.json();
                    setVolumeData(json.volumeByCategory || []);
                    setFrequencyData(json.weeklyFrequency || []);
                }
            } catch (error) {
                console.error("Failed to fetch stats", error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [user]);

    if (loading) return <div className="p-4 text-center">Loading stats...</div>;

    return (
        <div className="p-4 space-y-6 animate-fadeIn pb-24">
            <h1 className="text-2xl font-bold tracking-tight">대시보드</h1>

            <div className="grid gap-4 md:grid-cols-2">
                {/* Weekly Frequency Chart */}
                <div className="bg-surface rounded-xl p-4 shadow-sm border border-white/5 md:col-span-2">
                    <h2 className="text-lg font-semibold mb-4">이번 주 부위별 세트 수</h2>
                    <div style={{ width: '100%', height: 300 }}>
                        {frequencyData.length > 0 ? (
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={frequencyData} layout="vertical">
                                    <XAxis type="number" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} allowDecimals={false} />
                                    <YAxis dataKey="category" type="category" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} width={50} />
                                    <Tooltip
                                        cursor={{ fill: 'transparent' }}
                                        contentStyle={{ backgroundColor: '#1a1a1a', borderRadius: '8px', border: 'none' }}
                                        itemStyle={{ color: '#fff' }}
                                    />
                                    <Bar dataKey="count" radius={[0, 4, 4, 0]} barSize={20}>
                                        {frequencyData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill="#3b82f6" />
                                        ))}
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        ) : (
                            <div className="h-full flex items-center justify-center text-gray-500">
                                이번 주 운동 세트 기록이 없습니다.
                            </div>
                        )}
                    </div>
                </div>

                {/* Quick Stats */}
                <div className="bg-surface rounded-xl p-4 shadow-sm border border-white/5 col-span-1 md:col-span-2">
                    <h2 className="text-lg font-semibold mb-4">요약</h2>
                    <div className="grid grid-cols-2 gap-4">
                        <div className="flex flex-col items-center justify-center p-4 bg-background/50 rounded-lg">
                            <span className="text-gray-400 text-sm mb-1">현재 스트릭</span>
                            <span className="text-xl font-bold text-primary">연속 {user?.currentStreak || 0}일째 운동 중 🔥</span>
                        </div>
                        <div className="flex flex-col items-center justify-center p-4 bg-background/50 rounded-lg">
                            <span className="text-gray-400 text-sm mb-1">총 운동 부위</span>
                            <span className="text-xl font-bold">{volumeData.length}개</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
