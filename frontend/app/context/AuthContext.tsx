"use client";

import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getApiBaseUrl } from '../utils/apiUtils';

interface User {
    userId: number;
    username: string;
    currentStreak?: number;
}

interface AuthContextType {
    user: User | null;
    loading: boolean;
    login: (username: string, apiKey: string) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const router = useRouter();

    useEffect(() => {
        // Check localStorage for existing session
        const storedUser = localStorage.getItem('iron_user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = async (username: string, apiKey: string) => {
        try {
            const res = await fetch(`${getApiBaseUrl()}/api/user/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, apiKey }),
            });

            if (!res.ok) {
                throw new Error('Login failed');
            }

            const data = await res.json();
            const userData = {
                userId: data.userId,
                username: data.username,
                currentStreak: data.currentStreak
            };

            setUser(userData);
            localStorage.setItem('iron_user', JSON.stringify(userData));
            router.push('/'); // Default to dashboard after login
        } catch (error) {
            console.error("Login Error:", error);
            alert("Login failed. Please check your username (test1/test2).");
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('iron_user');
        router.push('/login');
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
