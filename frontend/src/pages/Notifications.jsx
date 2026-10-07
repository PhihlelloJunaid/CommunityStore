import { useEffect, useState } from 'react';
import { Bell, Check } from 'lucide-react';
import api from '../services/api';

export default function Notifications() {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const loadNotifications = async () => {
        try {
            setLoading(true);
            const { data } = await api.get('/notifications');
            setNotifications(data);
            setError('');
        } catch (err) {
            setError(
                err.response?.data?.message ||
                'Unable to load notifications.'
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadNotifications();
    }, []);

    const markAsRead = async (id) => {
        try {
            await api.put(`/notifications/${id}/read`);

            setNotifications((current) =>
                current.map((notification) =>
                    notification.id === id
                        ? { ...notification, read: true }
                        : notification
                )
            );
        } catch {
            setError('Unable to update notification.');
        }
    };

    const formatDate = (value) => {
        if (!value) return '';
        return new Date(value).toLocaleString('en-ZA', {
            dateStyle: 'medium',
            timeStyle: 'short'
        });
    };

    return (
        <main className="page-shell">
            <div className="container">
                <div className="page-heading">
                    <div>
                        <span className="eyebrow">Updates</span>
                        <h1>Notifications</h1>
                        <p>Stay up to date with your Community Store activity.</p>
                    </div>
                </div>

                {error && <div className="alert error">{error}</div>}

                {loading ? (
                    <div className="empty-state">
                        <p>Loading notifications...</p>
                    </div>
                ) : notifications.length === 0 ? (
                    <div className="empty-state">
                        <Bell size={34} />
                        <h3>No notifications</h3>
                        <p>You are all caught up.</p>
                    </div>
                ) : (
                    <div className="notification-list">
                        {notifications.map((notification) => (
                            <article
                                key={notification.id}
                                className={`notification-card ${
                                    notification.read ? 'read' : 'unread'
                                }`}
                            >
                                <div className="notification-icon">
                                    <Bell size={19} />
                                </div>

                                <div className="notification-content">
                                    <div className="notification-top">
                                        <div>
                                            <span className="eyebrow">
                                                {notification.type || 'SYSTEM'}
                                            </span>
                                            <h3>{notification.title}</h3>
                                        </div>

                                        {!notification.read && (
                                            <span className="notification-dot" />
                                        )}
                                    </div>

                                    <p>{notification.message}</p>

                                    <div className="notification-bottom">
                                        <small>
                                            {formatDate(notification.createdAt)}
                                        </small>

                                        {!notification.read && (
                                            <button
                                                className="ghost-btn small"
                                                onClick={() =>
                                                    markAsRead(notification.id)
                                                }
                                            >
                                                <Check size={15} />
                                                Mark as read
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </div>
        </main>
    );
}