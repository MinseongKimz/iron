"use client";

import { useAuth } from '../context/AuthContext';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function RouteGuard({ children }: { children: React.ReactNode }) {
    const { user, loading } = useAuth();
    const pathname = usePathname();
    const router = useRouter();

    useEffect(() => {
        if (!loading && !user && pathname !== '/login') {
            router.push('/login');
        }
    }, [user, loading, pathname, router]);

    // Show nothing while checking auth
    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="text-primary">Loading...</div>
        </div>;
    }

    // Allow login page always
    if (pathname === '/login') {
        return <>{children}</>;
    }

    // Require auth for all other pages
    if (!user) {
        return null; // Will redirect in useEffect
    }

    return <>{children}</>;
}
