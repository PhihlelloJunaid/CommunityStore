const PRODUCT_IMAGES = {
    'denim jacket': '/products/denim-jacket.jpg',
    'classic sneakers': '/products/classic-sneakers.jpg',
    'wireless headphones': '/products/wireless-headphones.jpg',
    'everyday backpack': '/products/everyday-backpack.jpg',
    'pre-loved handbag': '/products/pre-loved-handbag.jpg',
    'pre-owned smartphone': '/products/smartphone.jpg'
};

const CATEGORY_IMAGES = {
    clothing: '/products/denim-jacket.jpg',
    shoes: '/products/classic-sneakers.jpg',
    gadgets: '/products/wireless-headphones.jpg',
    accessories: '/products/everyday-backpack.jpg',
    electronics: '/products/smartphone.jpg'
};

export function getProductImage(product) {
    const name = (
        typeof product === 'string'
            ? product
            : product?.name || ''
    ).trim().toLowerCase();

    if (PRODUCT_IMAGES[name]) {
        return PRODUCT_IMAGES[name];
    }

    const category = (
        typeof product === 'object'
            ? product?.categoryName || product?.category?.name || ''
            : ''
    ).trim().toLowerCase();

    return CATEGORY_IMAGES[category] || '/products/denim-jacket.jpg';
}