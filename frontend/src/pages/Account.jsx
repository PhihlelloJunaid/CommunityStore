import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { UserRound, ShoppingBag, CreditCard } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api, { getErrorMessage } from '../services/api';

export default function Account() {
    const { user, logout } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        async function loadOrders() {
            if (!user?.id) {
                setLoading(false);
                return;
            }

            try {
                const response = await api.get(`/orders/user/${user.id}`);
                setOrders(response.data || []);
            } catch (error) {
                setError(getErrorMessage(error));
            } finally {
                setLoading(false);
            }
        }

        loadOrders();
    }, [user?.id]);

    const totalSpent = orders.reduce(
        (total, order) => total + Number(order.total || 0),
        0
    );

    if (!user) {
        return null;
    }

    return (
        <main className="container page">
            <div className="page-heading">
                <div>
                    <span className="eyebrow">My Account</span>
                    <h1>Account</h1>
                    <p>View your account details and shopping activity.</p>
                </div>

                <UserRound className="heading-icon" />
            </div>

            {error && <div className="alert error">{error}</div>}

            <section className="support-stats">
                <div className="feature">
                    <UserRound />
                    <span className="eyebrow">Role</span>
                    <h2>{user.role || 'CUSTOMER'}</h2>
                </div>

                <div className="feature">
                    <ShoppingBag />
                    <span className="eyebrow">Orders</span>
                    <h2>{orders.length}</h2>
                </div>

                <div className="feature">
                    <CreditCard />
                    <span className="eyebrow">Total Spent</span>
                    <h2>R {totalSpent.toFixed(2)}</h2>
                </div>
            </section>

            <section className="support-section">
                <div className="catalogue-heading">
                    <div>
                        <span className="eyebrow">Personal Details</span>
                        <h2>Your Information</h2>
                    </div>
                </div>

                <div className="profile-grid">
                    <div>
                        <span className="eyebrow">First Name</span>
                        <p>{user.firstName || 'Not provided'}</p>
                    </div>

                    <div>
                        <span className="eyebrow">Last Name</span>
                        <p>{user.lastName || 'Not provided'}</p>
                    </div>

                    <div>
                        <span className="eyebrow">Email</span>
                        <p>{user.email || 'Not provided'}</p>
                    </div>

                    <div>
                        <span className="eyebrow">Phone Number</span>
                        <p>{user.phoneNumber || 'Not provided'}</p>
                    </div>

                    <div>
                        <span className="eyebrow">Student Number</span>
                        <p>{user.studentNumber || 'Not provided'}</p>
                    </div>

                    <div>
                        <span className="eyebrow">Account Role</span>
                        <p>{user.role || 'CUSTOMER'}</p>
                    </div>
                </div>
            </section>

            <section className="support-section">
                <div className="catalogue-heading">
                    <div>
                        <span className="eyebrow">Shopping</span>
                        <h2>Orders</h2>
                    </div>

                    <Link to="/orders" className="secondary-btn">
                        View Orders
                    </Link>
                </div>

                {loading ? (
                    <div className="empty">
                        <h2>Loading...</h2>
                    </div>
                ) : orders.length === 0 ? (
                    <div className="empty">
                        <h2>No orders yet</h2>
                        <p>Your orders will appear here after checkout.</p>
                        <Link to="/products" className="primary-btn">
                            Browse Items
                        </Link>
                    </div>
                ) : (
                    <div className="support-table-wrap">
                        <table className="support-table">
                            <thead>
                            <tr>
                                <th>Order</th>
                                <th>Total</th>
                                <th>Status</th>
                            </tr>
                            </thead>

                            <tbody>
                            {orders.slice(0, 5).map((order) => (
                                <tr key={order.id}>
                                    <td>#{order.id}</td>
                                    <td>
                                        R {Number(order.total || 0).toFixed(2)}
                                    </td>
                                    <td>
                                        {order.status || 'UNKNOWN'}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            <div className="page-actions">
                <button className="secondary-btn" onClick={logout}>
                    Sign Out
                </button>
            </div>
        </main>
    );
}