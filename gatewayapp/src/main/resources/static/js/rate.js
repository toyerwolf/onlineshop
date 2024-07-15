import { checkTokenExpiration } from './auth.js';

// Функция для обновления звездочек рейтинга продукта
export function updateStars(productId, ratingValue) {
    const productCard = document.querySelector(`.product-card[data-product-id="${productId}"]`);
    if (productCard) {
        const stars = productCard.querySelectorAll('.rating .star');
        stars.forEach(star => {
            const starValue = parseInt(star.getAttribute('data-value'));
            if (starValue <= ratingValue) {
                star.textContent = '★';
                star.classList.add('active');
            } else {
                star.textContent = '☆';
                star.classList.remove('active');
            }
        });
    }
}

// Функция для отправки оценки продукта на сервер
export function rateProduct(starElement) {
    const productId = starElement.closest('.product-card').getAttribute('data-product-id');
    const ratingValue = parseInt(starElement.getAttribute('data-value'));

    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
        console.error('Access token not found. Unable to rate product.');
        return;
    }

    // Проверяем срок действия токенов перед отправкой запроса
    checkTokenExpiration()
        .then(async () => {
            const customerId = localStorage.getItem('customerId');

            const ratingData = {
                rating: ratingValue,
                productId: productId,
                customerId: customerId
            };

            // Отправляем запрос на сервер для оценки продукта
            const response = await fetch(`/ratings/${productId}/rate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken
                },
                body: JSON.stringify(ratingData)
            });

            if (!response.ok) {
                if (response.status === 401) {
                    logout(); // Вызываем logout при неавторизованном доступе
                    throw new Error('Unauthorized: User logged out');
                }
                throw new Error('Failed to rate product');
            }

            const data = await response.json();
            console.log('Product rated successfully:', data.message);

            // Сохраняем оценку в localStorage
            localStorage.setItem(`product_${productId}_rating`, ratingValue.toString());

            // Обновляем звездочки для текущего продукта
            updateStars(productId, ratingValue);

            // Обновляем средний рейтинг продукта после успешной оценки
            return updateAverageRating(productId);
        })
        .then(() => {
            console.log('Average rating updated successfully.');
        })
        .catch(error => {
            console.error('Error rating product:', error);
        });
}

// Функция для обновления среднего рейтинга продукта на странице
function updateAverageRating(productId) {
    return new Promise((resolve, reject) => {
        fetch(`/products/${productId}/rating`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(averageRating => {
                const productCard = document.querySelector(`.product-card[data-product-id="${productId}"]`);
                if (productCard) {
                    const averageRatingContainer = productCard.querySelector('.average-rating');
                    if (averageRatingContainer) {
                        averageRatingContainer.innerHTML = '';
                        for (let i = 1; i <= 5; i++) {
                            const star = document.createElement('span');
                            star.textContent = i <= averageRating ? '★' : '☆';
                            averageRatingContainer.appendChild(star);
                        }
                    }
                }
                resolve();
            })
            .catch(error => {
                console.error(`Error loading average rating for product ${productId}:`, error);
                reject(error);
            });
    });
}

// При загрузке страницы, устанавливаем слушателей на звездочки и загружаем оценки продуктов
document.addEventListener('DOMContentLoaded', function() {
    const productCards = document.querySelectorAll('.product-card');
    productCards.forEach(card => {
        const stars = card.querySelectorAll('.rating .star');
        stars.forEach(star => {
            star.addEventListener('click', function() {
                rateProduct(star);
            });
        });

        const productId = card.getAttribute('data-product-id');

        // Загружаем и отображаем средний рейтинг для текущего продукта при загрузке страницы
        updateAverageRating(productId)
            .then(() => {
                console.log(`Average rating for product ${productId} loaded successfully.`);
            })
            .catch(error => {
                console.error(`Error loading average rating for product ${productId}:`, error);
            });

        // Проверяем, есть ли сохраненная оценка в localStorage и обновляем звездочки
        const savedRating = localStorage.getItem(`product_${productId}_rating`);
        if (savedRating) {
            updateStars(productId, parseInt(savedRating));
        }
    });
});