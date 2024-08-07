import { decrementQuantity, incrementQuantity } from './script.js';
import { makeOrder } from './order.js';
import { rateProduct, updateStars } from './rate.js';
import { toggleFavorite, loadFavorites } from './favorites.js';

window.decrementQuantity = decrementQuantity;
window.incrementQuantity = incrementQuantity;
window.makeOrder = makeOrder;
window.rateProduct = rateProduct;
window.toggleFavorite = toggleFavorite;// Импортируем toggleFavorite и loadFavorites

document.addEventListener('DOMContentLoaded', () => {
    viewWishlist().then(() => {
        console.log('Wishlist loaded successfully');
        loadFavorites(); // Загружаем избранное при загрузке страницы
    }).catch(error => {
        console.error('Error loading wishlist:', error);
    });
});

async function viewWishlist() {
    const customerId = localStorage.getItem('customerId');
    const accessToken = localStorage.getItem('access_token');

    if (!customerId || !accessToken) {
        console.error('Customer ID or Access Token is missing');
        return;
    }

    try {
        const response = await fetch(`/wishlist/getAllProducts/${customerId}`, {
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (!response.ok) {
            console.error('Failed to fetch wishlist');
            return;
        }

        const products = await response.json();
        const wishlistSection = document.getElementById('wishlist-section');
        const wishlistContainer = document.createElement('div');
        wishlistContainer.id = 'wishlist-container';

        for (const product of products) {
            const productCard = document.createElement('div');
            productCard.className = 'product-card';
            productCard.dataset.productId = product.id;

            const isFavorite = product.isFavorite ? ' favorite' : '';

            // Получаем средний рейтинг продукта
            let averageRatingDisplay = 'Рейтинг отсутствует';
            try {
                const ratingResponse = await fetch(`/products/${product.id}/rating`);
                if (ratingResponse.ok) {
                    const averageRating = await ratingResponse.json();
                    if (averageRating !== null) {
                        averageRatingDisplay = averageRating.toFixed(1); // округляем до одного знака после запятой
                    }
                } else {
                    console.error('Failed to fetch product rating');
                }
            } catch (error) {
                console.error('Error fetching product rating:', error);
            }

            productCard.innerHTML = `
                <img src="${product.imageUrl}" alt="Product Image">
                <h3>${product.name}</h3>
                <p>${product.categoryName}</p>
                <p class="price">
                    ${product.discountPrice ? `
                        <span class="original-price">${product.price}</span>
                        <span class="discounted-price">${product.discountPrice}</span>
                    ` : `
                        <span>${product.price}</span>
                    `}
                </p>
                <div class="quantity">
                    <button class="quantity-btn" onclick="decrementQuantity(this)">-</button>
                    <label>
                        <input type="number" class="quantity-input" value="1" min="1">
                    </label>
                    <button class="quantity-btn" onclick="incrementQuantity(this)">+</button>
                </div>
                <div class="rating" data-product-id="${product.id}">
                    <span class="star" data-value="5" onclick="rateProduct(this)">☆</span>
                    <span class="star" data-value="4" onclick="rateProduct(this)">☆</span>
                    <span class="star" data-value="3" onclick="rateProduct(this)">☆</span>
                    <span class="star" data-value="2" onclick="rateProduct(this)">☆</span>
                    <span class="star" data-value="1" onclick="rateProduct(this)">☆</span>
                </div>
                <div class="average-rating">
                    ${averageRatingDisplay}
                </div>
                <button class="make-order-button" onclick="makeOrder(${product.id})">Make Order</button>
                <span class="heart${isFavorite}" onclick="toggleFavorite(this)">♡</span>
            `;

            wishlistContainer.appendChild(productCard);
        }

        wishlistSection.appendChild(wishlistContainer);
    } catch (error) {
        console.error('Error fetching wishlist items:', error);
    }
}
window.addEventListener('load', loadFavorites);