"use client";

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function HomePage() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/calendar');
  }, [router]);

  return (
    <div className="flex items-center justify-center h-screen">
      <p className="text-gray-400">Redirecting to Calendar...</p>
    </div>
  );
}
