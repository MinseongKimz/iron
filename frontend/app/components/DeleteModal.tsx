"use client";

import React from 'react';

interface DeleteModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
}

export default function DeleteModal({ isOpen, onClose, onConfirm }: DeleteModalProps) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black-70 backdrop-blur-sm p-4 animate-fadeIn">
            <div className="bg-surface border rounded-2xl w-full max-w-sm overflow-hidden shadow-2xl animate-zoomIn">
                <div className="p-6 text-center">
                    <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full mb-4" style={{ backgroundColor: 'var(--alert-bg)' }}>
                        <svg className="h-8 w-8 text-alert" fill="none" viewBox="0 0 24 24" strokeWidth="2" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                    </div>
                    <h3 className="text-xl font-bold text-foreground mb-2">기록 삭제</h3>
                    <p className="text-secondary text-sm break-keep">
                        정말 삭제하시겠습니까? 삭제된 기록은 복구할 수 없습니다.
                    </p>
                </div>
                <div className="flex border-t">
                    <button
                        onClick={onClose}
                        className="flex-1 py-4 text-sm font-semibold text-secondary hover:bg-surface-highlight transition-colors"
                    >
                        취소
                    </button>
                    <button
                        onClick={onConfirm}
                        className="flex-1 py-4 text-sm font-semibold text-alert hover:bg-surface-highlight transition-colors border-l"
                    >
                        삭제
                    </button>
                </div>
            </div>
        </div>
    );
}
