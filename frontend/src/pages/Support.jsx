import { useEffect, useState } from 'react';
import {
    AlertCircle,
    CheckCircle,
    Clock,
    CreditCard,
    RefreshCw,
    Send,
    ShoppingBag
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api, { getErrorMessage } from '../services/api';

export default function Support() {
    const { user } = useAuth();

    const isStaff =
        user?.role === 'CUSTOMER_SUPPORT' ||
        user?.role === 'ADMIN';

    const [requests, setRequests] = useState([]);
    const [orders, setOrders] = useState([]);
    const [payments, setPayments] = useState([]);

    const [subject, setSubject] = useState('');
    const [description, setDescription] = useState('');
    const [orderId, setOrderId] = useState('');

    const [activeTab, setActiveTab] = useState('requests');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');

    async function loadData() {
        try {
            setLoading(true);
            setError('');

            const requestResponse =
                await api.get('/support/requests');

            setRequests(requestResponse.data || []);

            if (isStaff) {
                const [ordersResponse, paymentsResponse] =
                    await Promise.all([
                        api.get('/orders'),
                        api.get('/payments')
                    ]);

                setOrders(ordersResponse.data || []);
                setPayments(paymentsResponse.data || []);
            } else {
                const ordersResponse =
                    await api.get(`/orders/user/${user.id}`);

                setOrders(ordersResponse.data || []);
            }
        } catch (error) {
            setError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user) {
            loadData();
        }
    }, [user?.id, isStaff]);

    async function submitRequest(event) {
        event.preventDefault();

        if (!subject.trim() || !description.trim()) {
            setError('Subject and description are required.');
            return;
        }

        try {
            setSubmitting(true);
            setError('');
            setMessage('');

            await api.post('/support/requests', {
                subject: subject.trim(),
                description: description.trim(),
                orderId: orderId ? Number(orderId) : null
            });

            setSubject('');
            setDescription('');
            setOrderId('');

            setMessage('Your support request has been submitted.');
            await loadData();
        } catch (error) {
            setError(getErrorMessage(error));
        } finally {
            setSubmitting(false);
        }
    }

    async function updateRequest(id, status) {
        try {
            setError('');
            setMessage('');

            await api.put(`/support/requests/${id}`, {
                status,
                response: `Request updated to ${status.replace('_', ' ').toLowerCase()}.`
            });

            setMessage('Support request updated.');
            await loadData();
        } catch (error) {
            setError(getErrorMessage(error));
        }
    }

    return (
        <main className="container page">
            <div className="page-heading">
                <div>
                    <span className="eyebrow">
                        {isStaff ? 'Customer Support' : 'Help Centre'}
                    </span>

                    <h1>
                        {isStaff
                            ? 'Support Dashboard'
                            : 'How can we help?'}
                    </h1>

                    <p>
                        {isStaff
                            ? 'Manage customer support requests, orders and payments.'
                            : 'Send us a request and track your support issues.'}
                    </p>
                </div>

                <AlertCircle className="heading-icon" />
            </div>

            {error && (
                <div className="alert error">
                    {error}
                </div>
            )}

            {message && (
                <div className="alert">
                    {message}
                </div>
            )}

            {!isStaff && (
                <>
                    <section className="support-section">
                        <div className="catalogue-heading">
                            <div>
                                <span className="eyebrow">
                                    Contact Support
                                </span>
                                <h2>Send a request</h2>
                            </div>
                        </div>

                        <form
                            className="form-grid"
                            onSubmit={submitRequest}
                        >
                            <div className="field">
                                <label htmlFor="subject">
                                    Subject
                                </label>

                                <input
                                    id="subject"
                                    value={subject}
                                    onChange={(event) =>
                                        setSubject(event.target.value)
                                    }
                                    placeholder="What do you need help with?"
                                />
                            </div>

                            <div className="field">
                                <label htmlFor="order">
                                    Related Order
                                </label>

                                <select
                                    id="order"
                                    value={orderId}
                                    onChange={(event) =>
                                        setOrderId(event.target.value)
                                    }
                                >
                                    <option value="">
                                        No order
                                    </option>

                                    {orders.map((order) => (
                                        <option
                                            key={order.id}
                                            value={order.id}
                                        >
                                            Order #{order.id}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="field full-field">
                                <label htmlFor="description">
                                    Description
                                </label>

                                <textarea
                                    id="description"
                                    rows="6"
                                    value={description}
                                    onChange={(event) =>
                                        setDescription(
                                            event.target.value
                                        )
                                    }
                                    placeholder="Describe the problem..."
                                />
                            </div>

                            <button
                                type="submit"
                                className="primary-btn"
                                disabled={submitting}
                            >
                                <Send size={17} />
                                {submitting
                                    ? 'Sending...'
                                    : 'Submit Request'}
                            </button>
                        </form>
                    </section>
                </>
            )}

            <div className="support-tabs">
                <button
                    className={
                        activeTab === 'requests'
                            ? 'primary-btn'
                            : 'secondary-btn'
                    }
                    onClick={() => setActiveTab('requests')}
                >
                    <AlertCircle size={16} />
                    Requests
                </button>

                {isStaff && (
                    <>
                        <button
                            className={
                                activeTab === 'orders'
                                    ? 'primary-btn'
                                    : 'secondary-btn'
                            }
                            onClick={() => setActiveTab('orders')}
                        >
                            <ShoppingBag size={16} />
                            Orders
                        </button>

                        <button
                            className={
                                activeTab === 'payments'
                                    ? 'primary-btn'
                                    : 'secondary-btn'
                            }
                            onClick={() => setActiveTab('payments')}
                        >
                            <CreditCard size={16} />
                            Payments
                        </button>
                    </>
                )}

                <button
                    className="secondary-btn"
                    onClick={loadData}
                    disabled={loading}
                >
                    <RefreshCw size={16} />
                    Refresh
                </button>
            </div>

            {loading ? (
                <div className="empty">
                    <h2>Loading...</h2>
                </div>
            ) : (
                <>
                    {activeTab === 'requests' && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Support
                                    </span>

                                    <h2>
                                        {isStaff
                                            ? 'Customer Requests'
                                            : 'My Requests'}
                                    </h2>
                                </div>

                                <span>{requests.length}</span>
                            </div>

                            {requests.length === 0 ? (
                                <div className="empty">
                                    <h2>No support requests</h2>
                                    <p>
                                        {isStaff
                                            ? 'Customer requests will appear here.'
                                            : 'You have not submitted any requests yet.'}
                                    </p>
                                </div>
                            ) : (
                                <div className="support-table-wrap">
                                    <table className="support-table">
                                        <thead>
                                        <tr>
                                            <th>Subject</th>

                                            {isStaff && (
                                                <th>Customer</th>
                                            )}

                                            <th>Order</th>
                                            <th>Status</th>
                                            <th>Created</th>

                                            {isStaff && (
                                                <th>Action</th>
                                            )}
                                        </tr>
                                        </thead>

                                        <tbody>
                                        {requests.map((request) => (
                                            <tr key={request.id}>
                                                <td>
                                                    <strong>
                                                        {request.subject}
                                                    </strong>
                                                    <div>
                                                        {request.description}
                                                    </div>
                                                </td>

                                                {isStaff && (
                                                    <td>
                                                        #{request.customerId}
                                                    </td>
                                                )}

                                                <td>
                                                    {request.orderId
                                                        ? `#${request.orderId}`
                                                        : '-'}
                                                </td>

                                                <td>
                                                        <span>
                                                            {request.status}
                                                        </span>
                                                </td>

                                                <td>
                                                    {request.createdAt
                                                        ? new Date(
                                                            request.createdAt
                                                        ).toLocaleDateString()
                                                        : '-'}
                                                </td>

                                                {isStaff && (
                                                    <td>
                                                        <div className="support-actions">
                                                            {request.status !== 'IN_PROGRESS' &&
                                                                request.status !== 'RESOLVED' &&
                                                                request.status !== 'CLOSED' && (
                                                                    <button
                                                                        className="secondary-btn"
                                                                        onClick={() =>
                                                                            updateRequest(
                                                                                request.id,
                                                                                'IN_PROGRESS'
                                                                            )
                                                                        }
                                                                    >
                                                                        <Clock size={15} />
                                                                        Start
                                                                    </button>
                                                                )}

                                                            {request.status !== 'RESOLVED' &&
                                                                request.status !== 'CLOSED' && (
                                                                    <button
                                                                        className="primary-btn"
                                                                        onClick={() =>
                                                                            updateRequest(
                                                                                request.id,
                                                                                'RESOLVED'
                                                                            )
                                                                        }
                                                                    >
                                                                        <CheckCircle size={15} />
                                                                        Resolve
                                                                    </button>
                                                                )}
                                                        </div>
                                                    </td>
                                                )}
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </section>
                    )}

                    {activeTab === 'orders' && isStaff && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Customer Orders
                                    </span>
                                    <h2>Orders</h2>
                                </div>

                                <span>{orders.length}</span>
                            </div>

                            <div className="support-table-wrap">
                                <table className="support-table">
                                    <thead>
                                    <tr>
                                        <th>Order</th>
                                        <th>Customer</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {orders.map((order) => (
                                        <tr key={order.id}>
                                            <td>#{order.id}</td>
                                            <td>
                                                {order.customerName ||
                                                    order.email ||
                                                    `Customer ${order.userId || ''}`}
                                            </td>
                                            <td>
                                                R{' '}
                                                {Number(
                                                    order.total || 0
                                                ).toFixed(2)}
                                            </td>
                                            <td>
                                                {order.status ||
                                                    'UNKNOWN'}
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {activeTab === 'payments' && isStaff && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Transactions
                                    </span>
                                    <h2>Payments</h2>
                                </div>

                                <span>{payments.length}</span>
                            </div>

                            <div className="support-table-wrap">
                                <table className="support-table">
                                    <thead>
                                    <tr>
                                        <th>Payment</th>
                                        <th>Order</th>
                                        <th>Amount</th>
                                        <th>Status</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {payments.map((payment) => (
                                        <tr key={payment.id}>
                                            <td>#{payment.id}</td>
                                            <td>
                                                #{payment.orderId}
                                            </td>
                                            <td>
                                                R{' '}
                                                {Number(
                                                    payment.amount || 0
                                                ).toFixed(2)}
                                            </td>
                                            <td>
                                                {payment.status ||
                                                    'UNKNOWN'}
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}
                </>
            )}
        </main>
    );
}