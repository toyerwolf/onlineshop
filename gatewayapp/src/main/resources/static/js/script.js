
export function incrementQuantity(element) {
    const productCard = element.closest('.product-card');
    const quantityInput = productCard.querySelector('.quantity-input');
    let value = parseInt(quantityInput.value);
    if (!isNaN(value)) {
        quantityInput.value = value + 1;
    }
}

export function decrementQuantity(element) {
    const productCard = element.closest('.product-card');
    const quantityInput = productCard.querySelector('.quantity-input');
    let value = parseInt(quantityInput.value);
    if (!isNaN(value) && value > 1) {
        quantityInput.value = value - 1;
    }
}

