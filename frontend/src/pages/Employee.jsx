import { useEffect, useMemo, useState } from 'react';
import {
    Package,
    Pencil,
    Plus,
    RefreshCw,
    ShoppingBag,
    Trash2
} from 'lucide-react';
import api, { getErrorMessage } from '../services/api';

export default function Employee() {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [orders, setOrders] = useState([]);

    const [form, setForm] = useState({
        id: null,
        name: '',
        description: '',
        price: '',
        quantity: '',
        categoryId: '',
        storeId: ''
    });

    const [editing, setEditing] = useState(false);
    const [section, setSection] = useState('dashboard');
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        try {
            setLoading(true);
            setError('');

            const [
                productsResponse,
                categoriesResponse,
                ordersResponse
            ] = await Promise.all([
                api.get('/products'),
                api.get('/categories'),
                api.get('/orders')
            ]);

            setProducts(productsResponse.data || []);
            setCategories(categoriesResponse.data || []);
            setOrders(ordersResponse.data || []);
        } catch (error) {
            console.error(error);
            setError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    }

    function updateForm(event) {
        const { name, value } = event.target;

        setForm((current) => ({
            ...current,
            [name]: value
        }));
    }

    function resetForm() {
        setForm({
            id: null,
            name: '',
            description: '',
            price: '',
            quantity: '',
            categoryId: '',
            storeId: ''
        });

        setEditing(false);
    }

    function editProduct(product) {
        setForm({
            id: product.id,
            name: product.name || '',
            description: product.description || '',
            price: product.price ?? '',
            quantity: product.quantity ?? '',
            categoryId: product.categoryId || '',
            storeId: product.storeId || ''
        });

        setEditing(true);
        setSection('items');
        setMessage('');
        setError('');
    }

    async function saveProduct(event) {
        event.preventDefault();
        setSaving(true);
        setMessage('');
        setError('');

        const payload = {
            name: form.name.trim(),
            description: form.description.trim(),
            price: Number(form.price),
            quantity: Number(form.quantity),
            categoryId: Number(form.categoryId),
            storeId: form.storeId
                ? Number(form.storeId)
                : null
        };

        try {
            if (editing) {
                const response = await api.put(
                    `/products/${form.id}`,
                    payload
                );

                setProducts((current) =>
                    current.map((product) =>
                        product.id === form.id
                            ? response.data
                            : product
                    )
                );

                setMessage('Item updated.');
            } else {
                const response = await api.post(
                    '/products',
                    payload
                );

                setProducts((current) => [
                    ...current,
                    response.data
                ]);

                setMessage('Item added.');
            }

            resetForm();
        } catch (error) {
            console.error(error);
            setError(getErrorMessage(error));
        } finally {
            setSaving(false);
        }
    }

    async function deleteProduct(id) {
        const confirmed = window.confirm(
            'Delete this item?'
        );

        if (!confirmed) {
            return;
        }

        try {
            setMessage('');
            setError('');

            await api.delete(`/products/${id}`);

            setProducts((current) =>
                current.filter((product) => product.id !== id)
            );

            setMessage('Item deleted.');

            if (form.id === id) {
                resetForm();
            }
        } catch (error) {
            console.error(error);
            setError(getErrorMessage(error));
        }
    }

    const availableItems = useMemo(
        () =>
            products.filter(
                (product) => Number(product.quantity) > 0
            ).length,
        [products]
    );

    const lowStockItems = useMemo(
        () =>
            products.filter(
                (product) =>
                    Number(product.quantity) > 0 &&
                    Number(product.quantity) <= 3
            ).length,
        [products]
    );

    const outOfStockItems = useMemo(
        () =>
            products.filter(
                (product) => Number(product.quantity) <= 0
            ).length,
        [products]
    );

    return (
        <main className="container page">
            <div className="page-heading">
                <div>
                    <span className="eyebrow">
                        Store
                    </span>

                    <h1>Store Employee</h1>

                    <p>
                        Manage items, stock and orders.
                    </p>
                </div>

                <Package className="heading-icon" />
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

            <div className="support-tabs">
                <button
                    className={
                        section === 'dashboard'
                            ? 'primary-btn'
                            : 'secondary-btn'
                    }
                    onClick={() =>
                        setSection('dashboard')
                    }
                >
                    Dashboard
                </button>

                <button
                    className={
                        section === 'items'
                            ? 'primary-btn'
                            : 'secondary-btn'
                    }
                    onClick={() =>
                        setSection('items')
                    }
                >
                    Items
                </button>

                <button
                    className={
                        section === 'inventory'
                            ? 'primary-btn'
                            : 'secondary-btn'
                    }
                    onClick={() =>
                        setSection('inventory')
                    }
                >
                    Inventory
                </button>

                <button
                    className={
                        section === 'orders'
                            ? 'primary-btn'
                            : 'secondary-btn'
                    }
                    onClick={() =>
                        setSection('orders')
                    }
                >
                    Orders
                </button>

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
                    {section === 'dashboard' && (
                        <section className="support-stats">
                            <div className="feature">
                                <Package />

                                <span className="eyebrow">
                                    Items
                                </span>

                                <h2>
                                    {products.length}
                                </h2>
                            </div>

                            <div className="feature">
                                <ShoppingBag />

                                <span className="eyebrow">
                                    Available
                                </span>

                                <h2>
                                    {availableItems}
                                </h2>
                            </div>

                            <div className="feature">
                                <Package />

                                <span className="eyebrow">
                                    Low Stock
                                </span>

                                <h2>
                                    {lowStockItems}
                                </h2>
                            </div>

                            <div className="feature">
                                <Package />

                                <span className="eyebrow">
                                    Out of Stock
                                </span>

                                <h2>
                                    {outOfStockItems}
                                </h2>
                            </div>

                            <div className="feature">
                                <ShoppingBag />

                                <span className="eyebrow">
                                    Orders
                                </span>

                                <h2>
                                    {orders.length}
                                </h2>
                            </div>
                        </section>
                    )}

                    {section === 'items' && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Items
                                    </span>

                                    <h2>
                                        Manage Items
                                    </h2>
                                </div>

                                <button
                                    className="primary-btn"
                                    onClick={() => {
                                        resetForm();
                                        setSection('items');
                                    }}
                                >
                                    <Plus size={16} />
                                    Add Item
                                </button>
                            </div>

                            <form
                                className="auth-card wide"
                                onSubmit={saveProduct}
                            >
                                <h2>
                                    {editing
                                        ? 'Edit Item'
                                        : 'Add Item'}
                                </h2>

                                <div className="form-grid">
                                    <label>
                                        Name

                                        <input
                                            name="name"
                                            value={form.name}
                                            onChange={updateForm}
                                            required
                                        />
                                    </label>

                                    <label>
                                        Price

                                        <input
                                            name="price"
                                            type="number"
                                            min="0"
                                            step="0.01"
                                            value={form.price}
                                            onChange={updateForm}
                                            required
                                        />
                                    </label>

                                    <label>
                                        Quantity

                                        <input
                                            name="quantity"
                                            type="number"
                                            min="0"
                                            value={form.quantity}
                                            onChange={updateForm}
                                            required
                                        />
                                    </label>

                                    <label>
                                        Category

                                        <select
                                            name="categoryId"
                                            value={form.categoryId}
                                            onChange={updateForm}
                                            required
                                        >
                                            <option value="">
                                                Select category
                                            </option>

                                            {categories.map(
                                                (category) => (
                                                    <option
                                                        key={
                                                            category.id
                                                        }
                                                        value={
                                                            category.id
                                                        }
                                                    >
                                                        {
                                                            category.name
                                                        }
                                                    </option>
                                                )
                                            )}
                                        </select>
                                    </label>
                                </div>

                                <label>
                                    Description

                                    <textarea
                                        name="description"
                                        value={form.description}
                                        onChange={updateForm}
                                        rows="4"
                                    />
                                </label>

                                <label>
                                    Store ID

                                    <input
                                        name="storeId"
                                        type="number"
                                        min="1"
                                        value={form.storeId}
                                        onChange={updateForm}
                                    />
                                </label>

                                <div className="hero-actions">
                                    <button
                                        className="primary-btn"
                                        disabled={saving}
                                    >
                                        {saving
                                            ? 'Saving...'
                                            : editing
                                                ? 'Save Changes'
                                                : 'Add Item'}
                                    </button>

                                    {editing && (
                                        <button
                                            type="button"
                                            className="secondary-btn"
                                            onClick={resetForm}
                                        >
                                            Cancel
                                        </button>
                                    )}
                                </div>
                            </form>

                            <div className="product-grid">
                                {products.map((product) => (
                                    <article
                                        className="product-card"
                                        key={product.id}
                                    >
                                        <div className="product-body">
                                            <div className="eyebrow">
                                                {product.categoryName ||
                                                    'Item'}
                                            </div>

                                            <h3>
                                                {product.name}
                                            </h3>

                                            <p>
                                                {product.description ||
                                                    'No description'}
                                            </p>

                                            <div className="product-bottom">
                                                <strong>
                                                    R{' '}
                                                    {Number(
                                                        product.price
                                                    ).toFixed(2)}
                                                </strong>

                                                <span className="stock in">
                                                    {product.quantity}{' '}
                                                    in stock
                                                </span>
                                            </div>

                                            <div className="hero-actions">
                                                <button
                                                    className="secondary-btn"
                                                    onClick={() =>
                                                        editProduct(
                                                            product
                                                        )
                                                    }
                                                >
                                                    <Pencil size={16} />
                                                    Edit
                                                </button>

                                                <button
                                                    className="secondary-btn"
                                                    onClick={() =>
                                                        deleteProduct(
                                                            product.id
                                                        )
                                                    }
                                                >
                                                    <Trash2 size={16} />
                                                    Delete
                                                </button>
                                            </div>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        </section>
                    )}

                    {section === 'inventory' && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Inventory
                                    </span>

                                    <h2>
                                        Stock
                                    </h2>
                                </div>
                            </div>

                            <div className="support-table-wrap">
                                <table className="support-table">
                                    <thead>
                                    <tr>
                                        <th>Item</th>
                                        <th>Category</th>
                                        <th>Quantity</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {products.map((product) => {
                                        const quantity =
                                            Number(
                                                product.quantity
                                            );

                                        return (
                                            <tr
                                                key={
                                                    product.id
                                                }
                                            >
                                                <td>
                                                    {
                                                        product.name
                                                    }
                                                </td>

                                                <td>
                                                    {
                                                        product.categoryName
                                                    }
                                                </td>

                                                <td>
                                                    {quantity}
                                                </td>

                                                <td>
                                                    {quantity <= 0
                                                        ? 'Out of Stock'
                                                        : quantity <=
                                                        3
                                                            ? 'Low Stock'
                                                            : 'Available'}
                                                </td>

                                                <td>
                                                    <button
                                                        className="secondary-btn"
                                                        onClick={() =>
                                                            editProduct(
                                                                product
                                                            )
                                                        }
                                                    >
                                                        <Pencil
                                                            size={16}
                                                        />
                                                        Edit
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    )}

                    {section === 'orders' && (
                        <section className="support-section">
                            <div className="catalogue-heading">
                                <div>
                                    <span className="eyebrow">
                                        Orders
                                    </span>

                                    <h2>
                                        Customer Orders
                                    </h2>
                                </div>
                            </div>

                            {orders.length === 0 ? (
                                <div className="empty">
                                    <h2>No orders found</h2>
                                </div>
                            ) : (
                                <div className="support-table-wrap">
                                    <table className="support-table">
                                        <thead>
                                        <tr>
                                            <th>
                                                Order
                                            </th>
                                            <th>
                                                Customer
                                            </th>
                                            <th>
                                                Total
                                            </th>
                                            <th>
                                                Status
                                            </th>
                                        </tr>
                                        </thead>

                                        <tbody>
                                        {orders.map(
                                            (order) => (
                                                <tr
                                                    key={
                                                        order.id
                                                    }
                                                >
                                                    <td>
                                                        #
                                                        {
                                                            order.id
                                                        }
                                                    </td>

                                                    <td>
                                                        {
                                                            order.customerName ||
                                                            order.email ||
                                                            `Customer ${order.userId || ''}`
                                                        }
                                                    </td>

                                                    <td>
                                                        R{' '}
                                                        {Number(
                                                            order.total ||
                                                            0
                                                        ).toFixed(2)}
                                                    </td>

                                                    <td>
                                                        {
                                                            order.status ||
                                                            'UNKNOWN'
                                                        }
                                                    </td>
                                                </tr>
                                            )
                                        )}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </section>
                    )}
                </>
            )}
        </main>
    );
}