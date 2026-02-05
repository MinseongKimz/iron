"use client";

import React from 'react';

type WorkoutSet = {
    setOrder: number;
    weight: number;
    reps: number;
};

type Exercise = {
    name: string;
    sets: WorkoutSet[];
    totalVolume?: number;
};

type WorkoutCardProps = {
    summary: string;
    exercises: Exercise[];
    onDelete?: () => void;
};

export default function WorkoutCard({ summary, exercises, onDelete }: WorkoutCardProps) {
    return (
        <div className="card border-l-4 border-primary mt-2">
            <h4 className="font-bold text-primary mb-2">💪 운동 분석 완료</h4>
            <p className="text-sm text-gray-300 mb-4">{summary}</p>

            <div className="space-y-3">
                {exercises.map((exercise, idx) => (
                    <div key={idx} className="bg-black/30 p-3 rounded">
                        <div className="flex justify-between items-center mb-2">
                            <span className="font-semibold text-sm">{exercise.name}</span>
                            <span className="text-xs text-secondary">
                                {exercise.sets.length}세트
                            </span>
                        </div>

                        <div className="grid grid-cols-3 gap-2 text-xs text-gray-400 border-b border-gray-700 pb-1 mb-1">
                            <div className="text-center">세트</div>
                            <div className="text-center">무게(kg)</div>
                            <div className="text-center">횟수</div>
                        </div>

                        {exercise.sets.map((set, idx) => (
                            <div key={set.setOrder || idx} className="grid grid-cols-3 gap-2 text-sm">
                                <div className="text-center text-secondary">{set.setOrder}</div>
                                <div className="text-center font-mono">{set.weight}</div>
                                <div className="text-center font-mono">{set.reps}</div>
                            </div>
                        ))}
                    </div>
                ))}
            </div>

            <div className="mt-3 flex justify-end gap-2 text-xs">
                <button className="text-secondary hover:text-white">수정</button>
                <button
                    onClick={onDelete}
                    className="text-alert hover:text-red-400"
                >
                    삭제
                </button>
            </div>
        </div>
    );
}
