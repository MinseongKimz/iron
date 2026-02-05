"use client";

import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function BottomNav() {
    const pathname = usePathname();

    // Hide on login page
    if (pathname === '/login') return null;

    const navItems = [
        { href: '/chat', icon: '🏋️', label: '기록' },
        { href: '/calendar', icon: '📅', label: '달력' },
    ];

    return (
        <nav className="fixed bottom-0 left-0 right-0 bg-surface border-t border-gray-800">
            <div className="flex justify-around items-center h-16">
                {navItems.map(item => (
                    <Link
                        key={item.href}
                        href={item.href}
                        className={`flex flex-col items-center gap-1 flex-1 ${pathname === item.href ? 'text-primary' : 'text-gray-500'
                            }`}
                    >
                        <span className="text-2xl">{item.icon}</span>
                        <span className="text-xs">{item.label}</span>
                    </Link>
                ))}
            </div>
        </nav>
    );
}
