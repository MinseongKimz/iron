"use client";

import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/navigation';

export default function LogoutButton() {
    const { logout } = useAuth();
    const router = useRouter();

    const handleLogout = () => {
        logout();
        router.push('/login');
    };

    return (
        <button
            onClick={handleLogout}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-gray-800 text-gray-300 rounded-lg hover:bg-gray-700 hover:text-white transition-all text-sm font-medium"
            title="로그아웃"
        >
            <span>🚪</span>
            <span>로그아웃</span>
        </button>
    );
}
