const imageMap = {
    'Denim Jacket': '/products/denim-jacket.jpg',
    'Classic Sneakers': '/products/classic-sneakers.jpg',
    'Wireless Headphones': '/products/wireless-headphones.jpg',
    'Everyday Backpack': '/products/everyday-backpack.jpg',
    'Pre-loved Handbag': '/products/pre-loved-handbag.jpg',
    'Pre-owned Smartphone': '/products/smartphone.jpg'
};

const categoryImages = {
    Clothing: '/products/denim-jacket.jpg',
    Shoes: '/products/classic-sneakers.jpg',
    Gadgets: '/products/wireless-headphones.jpg',
    Accessories: '/products/everyday-backpack.jpg',
    Electronics: '/products/smartphone.jpg',
    Stationery: '/products/campus-notebook.svg'
};

export function getProductImage(product) {
    if (product?.imageUrl) {
        return product.imageUrl;
    }

    const name = String(product?.name || '').trim();

    if (imageMap[name]) {
        return imageMap[name];
    }

    if (categoryImages[product?.categoryName]) {
        return categoryImages[product.categoryName];
    }

    return '/products/community-item.svg';
}

export function formatCondition(condition) {
    return String(condition || 'GOOD')
        .replaceAll('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (letter) => letter.toUpperCase());
}