import type { Metadata } from "next";
import localFont from "next/font/local";
import "./globals.css";
import BottomNav from "./components/BottomNav";
import { AuthProvider } from "./context/AuthContext";
import RouteGuard from "./components/RouteGuard";

export const metadata: Metadata = {
  title: "Iron Secretary",
  description: "Your AI Workout Assistant",
  manifest: "/manifest.json",
  themeColor: "#121212",
  viewport: "width=device-width, initial-scale=1, maximum-scale=1"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning={true}>
      <body>
        <AuthProvider>
          <RouteGuard>
            <div className="container">
              {children}
            </div>
            <BottomNav />
          </RouteGuard>
        </AuthProvider>
      </body>
    </html>
  );
}
