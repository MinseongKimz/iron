"use client";

import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import { getApiBaseUrl } from '../utils/apiUtils';
import { useAuth } from '../context/AuthContext';
import EditModal from '../components/EditModal';
import DeleteModal from '../components/DeleteModal';
import LogoutButton from '../components/LogoutButton';

// Types
interface DailyWorkout {
    sessionId: string;
    summary: string;
    rawInput: string;
    exercises: Exercise[];
}

interface Exercise {
    name: string;
    sets: WorkoutSet[];
}

interface WorkoutSet {
    setId?: string; // Optional for compatibility
    setOrder: number;
    weight: number;
    reps: number;
}

export default function CalendarPage() {
    const { user } = useAuth();
    const [date, setDate] = useState<Date>(new Date());
    const [workouts, setWorkouts] = useState<DailyWorkout[]>([]);
    const [monthlyDates, setMonthlyDates] = useState<string[]>([]);
    const [editingWorkout, setEditingWorkout] = useState<DailyWorkout | null>(null);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false); // Added state for delete modal
    const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null); // Added state for selected session to delete

    // Fetch monthly status when date changes (month view) or User changes
    useEffect(() => {
        if (user) {
            fetchMonthlyStatus(date.getFullYear(), date.getMonth() + 1);
        }
    }, [date.getMonth(), user]);

    // Fetch daily details when date selected or User changes
    useEffect(() => {
        if (user) {
            fetchDailyDetails(date);
        }
    }, [date, user]);

    const fetchMonthlyStatus = async (year: number, month: number) => {
        if (!user) return;
        try {
            const res = await fetch(`${getApiBaseUrl()}/api/workout/history/monthly?year=${year}&month=${month}&userId=${user.userId}`);
            if (res.ok) {
                const dates = await res.json();
                setMonthlyDates(dates);
            }
        } catch (e) {
            console.error(e);
        }
    };

    const fetchDailyDetails = async (selectedDate: Date) => {
        if (!user) return;
        // Format date without timezone conversion
        const year = selectedDate.getFullYear();
        const month = String(selectedDate.getMonth() + 1).padStart(2, '0');
        const day = String(selectedDate.getDate()).padStart(2, '0');
        const dateStr = `${year}-${month}-${day}`;

        try {
            const res = await fetch(`${getApiBaseUrl()}/api/workout/history/${dateStr}?userId=${user.userId}`);
            if (res.ok) {
                const data = await res.json();
                setWorkouts(data);
            }
        } catch (e) {
            console.error(e);
        }
    };

    const handleDeleteClick = (sessionId: string) => {
        setSelectedSessionId(sessionId);
        setDeleteModalOpen(true);
    };

    const confirmDelete = async () => {
        if (!selectedSessionId) return;

        try {
            const res = await fetch(`${getApiBaseUrl()}/api/workout/session/${selectedSessionId}`, {
                method: 'DELETE'
            });
            if (res.ok) {
                setWorkouts(prev => prev.filter(w => w.sessionId !== selectedSessionId));
                if (user) fetchMonthlyStatus(date.getFullYear(), date.getMonth() + 1);
                // alert("삭제되었습니다!"); // Optional: Success feedback is often better as a toast or just UI update
            } else {
                alert("삭제 실패");
            }
        } catch (e) {
            console.error(e);
            alert("오류 발생");
        } finally {
            setDeleteModalOpen(false);
            setSelectedSessionId(null);
        }
    };

    const handleEdit = (workout: DailyWorkout) => {
        setEditingWorkout(workout);
    };

    const handleSaveEdit = async (
        setEdits: { setId: string; weight: number; reps: number }[],
        deleteSetIds: string[],
        newSets: { exerciseName: string; weight: number; reps: number; setOrder: number }[]
    ) => {
        if (!editingWorkout) return;

        try {
            const payload = {
                setEdits,
                deleteSetIds,
                newSets: newSets.map(s => ({ ...s, sessionId: editingWorkout.sessionId }))
            };

            const res = await fetch(`${getApiBaseUrl()}/api/workout/sets`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                setEditingWorkout(null);
                fetchDailyDetails(date);
                // alert("수정 완료!"); // User prefers less prompts? Or keep it? kept for now.
            } else {
                alert("수정 실패");
            }
        } catch (e) {
            console.error(e);
            alert("오류 발생");
        }
    };

    // Tile content for Green Dot
    const tileContent = ({ date, view }: { date: Date, view: string }) => {
        if (view === 'month') {
            const width = date.getDate() < 10 ? '0' : '';
            const month = (date.getMonth() + 1) < 10 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1);
            // Need to match format 'YYYY-MM-DD' returned by API
            const dateStr = `${date.getFullYear()}-${month}-${width}${date.getDate()}`;

            // Re-using local date calculation as API might return plain string
            // Actually, simplified ISO check:
            const isoDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().split('T')[0];

            if (monthlyDates.includes(isoDate) || monthlyDates.includes(dateStr)) {
                return <div style={{
                    width: '6px',
                    height: '6px',
                    backgroundColor: '#22c55e',
                    borderRadius: '50%',
                    margin: '4px auto 0'
                }}></div>;
            }
        }
        return null;
    };

    // Tile class name for Saturday blue text
    const tileClassName = ({ date, view }: { date: Date, view: string }) => {
        if (view === 'month' && date.getDay() === 6) {
            return 'saturday-tile';
        }
        return null;
    };

    if (!user) {
        return <div className="p-4 text-center text-gray-400">로그인이 필요합니다.</div>;
    }

    return (
        <div className="container min-h-screen pb-24 bg-background text-foreground">
            <header className="py-6 flex justify-between items-center sticky top-0 bg-background-90 backdrop-blur-md z-10 border-b px-4 -mx-4 mb-6">
                <h1 className="text-2xl font-bold tracking-tight">Workflow 📅</h1>
                <LogoutButton />
            </header>

            {/* Custom Styled Calendar Wrapper */}
            <div className="mb-8 p-1 bg-surface rounded-2xl border shadow-sm">
                <style jsx global>{`
                    .saturday-tile abbr {
                        color: #3b82f6 !important;
                    }
                `}</style>
                <Calendar
                    onChange={(val) => setDate(val as Date)}
                    value={date}
                    tileContent={tileContent}
                    tileClassName={tileClassName}
                    formatDay={(locale, date) => date.getDate().toString()}
                    next2Label={null}
                    prev2Label={null}
                    className="w-full bg-transparent border-none font-sans text-foreground"
                />
            </div>

            <div className="space-y-6">
                <h2 className="text-xl font-bold flex items-center gap-2">
                    <span className="text-primary">{date.getMonth() + 1}.{date.getDate()}</span>
                    <span>Workout</span>
                </h2>

                {workouts.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-8 bg-surface-50 rounded-2xl border border-dashed text-center">
                        <div className="w-12 h-12 bg-surface-highlight rounded-full flex items-center justify-center mb-4 text-secondary">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                            </svg>
                        </div>
                        <p className="font-semibold text-foreground">기록이 없습니다</p>
                        <p className="text-sm text-secondary mt-1">오늘의 운동을 AI 코치에게 알려주세요!</p>
                    </div>
                ) : (
                    workouts.map(workout => (
                        <div key={workout.sessionId} className="bg-surface rounded-2xl overflow-hidden border shadow-md transition-all hover:shadow-lg">
                            {/* Card Header */}
                            <div className="p-5 border-b flex justify-between items-start">
                                <div className="flex-1 pr-4">
                                    <div className="text-xs font-bold text-primary mb-1 uppercase tracking-wider">AI Feedback</div>
                                    <p className="text-sm leading-relaxed text-foreground italic">"{workout.summary || "피드백이 없습니다."}"</p>
                                </div>
                                <div className="flex gap-2 shrink-0">
                                    <button onClick={() => handleEdit(workout)} className="p-2 rounded-lg bg-surface-highlight hover:text-primary transition-colors">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                        </svg>
                                    </button>
                                    <button onClick={() => handleDeleteClick(workout.sessionId)} className="p-2 rounded-lg bg-surface-highlight hover:text-alert transition-colors">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                        </svg>
                                    </button>
                                </div>
                            </div>

                            {/* Exercises List (Mobile Friendly) */}
                            <div className="p-4 space-y-4">
                                {workout.exercises.map((ex, exIdx) => (
                                    <div key={exIdx} className="bg-background rounded-xl p-3 border border-surface-highlight">
                                        <h3 className="font-bold text-foreground mb-3 flex items-center gap-2">
                                            <span className="w-1 h-4 bg-primary rounded-full"></span>
                                            {ex.name}
                                        </h3>
                                        <div className="grid grid-cols-3 gap-2 text-center">
                                            {ex.sets.map((set, setIdx) => (
                                                <div key={`${exIdx}-${setIdx}`} className="bg-surface p-2 rounded-lg border relative overflow-hidden group">
                                                    <div className="text-[10px] text-secondary mb-0.5">SET {set.setOrder}</div>
                                                    <div className="font-mono font-bold text-foreground">
                                                        {set.weight} <span className="text-[10px] font-normal text-secondary">kg</span>
                                                    </div>
                                                    <div className="text-xs text-secondary">
                                                        {set.reps} reps
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Edit Modal */}
            {editingWorkout && (
                <EditModal
                    exercises={editingWorkout.exercises}
                    onClose={() => setEditingWorkout(null)}
                    onSave={handleSaveEdit}
                />
            )}

            {/* Delete Modal */}
            <DeleteModal
                isOpen={deleteModalOpen}
                onClose={() => setDeleteModalOpen(false)}
                onConfirm={confirmDelete}
            />
        </div>
    );
}
