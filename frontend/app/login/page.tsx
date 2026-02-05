"use client";

import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import styles from './login.module.css';

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [apiKey, setApiKey] = useState('');
    const { login } = useAuth();
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        await login(username, apiKey);
        setIsLoading(false);
    };

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h1 className={styles.title}>Iron Login</h1>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.inputGroup}>
                        <label>Username</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="test1 or test2"
                            required
                        />
                    </div>
                    <div className={styles.inputGroup}>
                        <label>Gemini API Key</label>
                        <input
                            type="password"
                            value={apiKey}
                            onChange={(e) => setApiKey(e.target.value)}
                            placeholder="Enter your API Key"
                        // Not required strictly if just logging in, but "User registers and enters key". 
                        // We'll leave it optional for login if key is already there, but UI suggests entering it.
                        />
                    </div>
                    <button type="submit" disabled={isLoading} className={styles.button}>
                        {isLoading ? 'Logging in...' : 'Enter'}
                    </button>
                </form>
            </div>
        </div>
    );
}
