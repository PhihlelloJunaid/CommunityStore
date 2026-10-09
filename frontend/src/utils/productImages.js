const PRODUCT_IMAGES = {
    'denim jacket': '/products/denim-jacket.jpg',
    'classic grey hoodie': '/products/denim-jacket.jpg',
    'classic sneakers': '/products/classic-sneakers.jpg',
    'wireless headphones': '/products/wireless-headphones.jpg',
    'everyday backpack': '/products/everyday-backpack.jpg',
    'pre-loved handbag': '/products/pre-loved-handbag.jpg',
    'pre-owned smartphone': '/products/smartphone.jpg',
    smartphone: '/products/smartphone.jpg'
};

const CATEGORY_IMAGES = {
    clothing: '/products/denim-jacket.jpg',
    shoes: '/products/classic-sneakers.jpg',
    gadgets: '/products/wireless-headphones.jpg',
    accessories: '/products/everyday-backpack.jpg',
    electronics: '/products/smartphone.jpg'
};

export const productImages = {
    ...PRODUCT_IMAGES,
    ...CATEGORY_IMAGES,
    Clothing: CATEGORY_IMAGES.clothing,
    Shoes: CATEGORY_IMAGES.shoes,
    Gadgets: CATEGORY_IMAGES.gadgets,
    Accessories: CATEGORY_IMAGES.accessories,
    Electronics: CATEGORY_IMAGES.electronics
};

export function getProductImage(productOrName, categoryName = '') {
    const isObject =
        productOrName !== null &&
        typeof productOrName === 'object';

    const name = (
        isObject
            ? productOrName.name || productOrName.title || ''
            : productOrName || ''
    ).trim().toLowerCase();

    const categoryValue = isObject
        ? productOrName.categoryName ||
        productOrName.category?.name ||
        productOrName.category ||
        categoryName
        : categoryName;

    const category =
        typeof categoryValue === 'string'
            ? categoryValue.trim().toLowerCase()
            : '';

    return (
        PRODUCT_IMAGES[name] ||
        CATEGORY_IMAGES[category] ||
        '/products/denim-jacket.jpg'
    );
}

export default getProductImage;