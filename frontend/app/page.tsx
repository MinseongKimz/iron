"use client";

import Dashboard from './components/Dashboard';

export default function HomePage() {
  return (
    <div className="flex flex-col h-screen bg-background text-foreground">
      <main className="flex-1 overflow-y-auto">
        <Dashboard />
      </main>
    </div>
  );
}
