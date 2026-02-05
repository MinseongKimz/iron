"use client";

import React, { useState } from 'react';

interface WorkoutSet {
    setId?: string;
    setOrder: number;
    weight: number;
    reps: number;
}

interface Exercise {
    name: string;
    sets: WorkoutSet[];
}

interface EditModalProps {
    exercises: Exercise[];
    onClose: () => void;
    onSave: (
        setEdits: { setId: string; weight: number; reps: number }[],
        deletedSetIds: string[],
        newSets: { exerciseName: string; weight: number; reps: number; setOrder: number }[]
    ) => void;
}

export default function EditModal({ exercises, onClose, onSave }: EditModalProps) {
    const [localExercises, setLocalExercises] = useState<Exercise[]>(JSON.parse(JSON.stringify(exercises)));
    const [deletedSetIds, setDeletedSetIds] = useState<string[]>([]);

    const handleChange = (exIdx: number, setIdx: number, field: 'weight' | 'reps', value: string) => {
        setLocalExercises(prev => {
            const newState = prev.map((ex, i) => {
                if (i !== exIdx) return ex;
                const newSets = [...ex.sets];
                newSets[setIdx] = {
                    ...newSets[setIdx],
                    [field]: parseFloat(value) || 0
                };
                return { ...ex, sets: newSets };
            });
            return newState;
        });
    };

    const handleAddSet = (e: React.MouseEvent, exIdx: number) => {
        e.preventDefault();
        e.stopPropagation();

        setLocalExercises(prev => {
            const newState = prev.map((ex, i) => {
                if (i !== exIdx) return ex;
                const newSets = [...ex.sets];
                const lastSet = newSets[newSets.length - 1];
                newSets.push({
                    setOrder: newSets.length + 1,
                    weight: lastSet ? lastSet.weight : 0,
                    reps: lastSet ? lastSet.reps : 10,
                });
                return { ...ex, sets: newSets };
            });
            return newState;
        });
    };

    const handleDeleteSet = (e: React.MouseEvent, exIdx: number, setIdx: number) => {
        e.preventDefault();
        e.stopPropagation();

        setLocalExercises(prev => {
            const newState = prev.map((ex, i) => {
                if (i !== exIdx) return ex;

                const set = ex.sets[setIdx];
                if (set.setId) {
                    setDeletedSetIds(ids => [...ids, set.setId!]);
                }

                const newSets = ex.sets.filter((_, idx) => idx !== setIdx);

                return { ...ex, sets: newSets };
            });
            return newState;
        });
    };

    const handleSave = () => {
        const setEdits: { setId: string; weight: number; reps: number }[] = [];
        const newSets: { exerciseName: string; weight: number; reps: number; setOrder: number }[] = [];

        localExercises.forEach(ex => {
            ex.sets.forEach((set, currentIdx) => {
                if (set.setId) {
                    setEdits.push({ setId: set.setId, weight: set.weight, reps: set.reps });
                } else {
                    newSets.push({
                        exerciseName: ex.name,
                        weight: set.weight,
                        reps: set.reps,
                        setOrder: currentIdx + 1
                    });
                }
            });
        });

        onSave(setEdits, deletedSetIds, newSets);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
            <div className="bg-surface border rounded-2xl w-full shadow-2xl flex flex-col" style={{ maxWidth: '400px', maxHeight: '70vh' }}>
                {/* Header */}
                <div className="p-4 border-b flex justify-between items-center shrink-0">
                    <h2 className="text-base font-bold text-foreground">운동 세트 수정</h2>
                    <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-surface-highlight text-secondary hover:text-foreground transition-colors">
                        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {/* Scrollable Content */}
                <div className="overflow-y-auto p-4 space-y-5 flex-1">
                    {localExercises.map((ex, exIdx) => (
                        <div key={exIdx} className="bg-background rounded-xl p-3 border border-surface-highlight">
                            <div className="flex justify-between items-center mb-2">
                                <h3 className="font-bold text-foreground flex items-center gap-2 text-sm">
                                    <span className="w-1 h-4 bg-primary rounded-full"></span>
                                    {ex.name}
                                </h3>
                                <button
                                    type="button"
                                    onClick={(e) => handleAddSet(e, exIdx)}
                                    className="px-2.5 py-1 rounded-md bg-surface-highlight text-[11px] text-secondary hover:text-primary transition-colors font-medium"
                                >
                                    + 추가
                                </button>
                            </div>

                            <div className="grid grid-cols-3 gap-2">
                                {ex.sets.map((set, setIdx) => (
                                    <div key={setIdx} className="bg-surface p-2 rounded-lg border">
                                        <div className="flex justify-between items-center mb-1">
                                            <div className="text-[10px] text-secondary font-medium">SET {setIdx + 1}</div>
                                            <button
                                                type="button"
                                                onClick={(e) => handleDeleteSet(e, exIdx, setIdx)}
                                                className="text-[11px] text-secondary hover:text-alert transition-colors font-medium"
                                            >
                                                ×
                                            </button>
                                        </div>

                                        <input
                                            type="number"
                                            step="0.5"
                                            value={set.weight}
                                            onChange={(e) => handleChange(exIdx, setIdx, 'weight', e.target.value)}
                                            className="w-full bg-background border border-surface-highlight rounded px-1.5 py-0.5 text-center text-sm font-mono font-bold text-foreground focus:border-primary focus:outline-none mb-1"
                                        />
                                        <div className="text-[10px] text-secondary text-center mb-1">kg</div>

                                        <input
                                            type="number"
                                            value={set.reps}
                                            onChange={(e) => handleChange(exIdx, setIdx, 'reps', e.target.value)}
                                            className="w-full bg-background border border-surface-highlight rounded px-1.5 py-0.5 text-center text-xs text-foreground focus:border-primary focus:outline-none mb-1"
                                        />
                                        <div className="text-[10px] text-secondary text-center">reps</div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>

                {/* Footer */}
                <div className="p-3 border-t flex justify-end gap-2 shrink-0">
                    <button
                        type="button"
                        onClick={onClose}
                        className="px-4 py-2 rounded-lg text-sm font-semibold text-secondary hover:bg-surface-highlight hover:text-foreground transition-colors"
                    >
                        취소
                    </button>
                    <button
                        type="button"
                        onClick={handleSave}
                        className="px-4 py-2 rounded-lg text-sm font-bold text-black bg-primary hover:opacity-90 transition-all"
                    >
                        저장
                    </button>
                </div>
            </div>
        </div>
    );
}
