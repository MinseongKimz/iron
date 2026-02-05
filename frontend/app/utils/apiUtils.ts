export const getApiBaseUrl = () => {
    if (typeof window !== 'undefined') {
        // Dynamically use the current hostname but point to port 8080 for backend
        return `${window.location.protocol}//${window.location.hostname}:8080`;
    }
    return 'http://localhost:8080';
};
