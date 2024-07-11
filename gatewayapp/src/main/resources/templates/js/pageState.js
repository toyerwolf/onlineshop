// let pageStateHistory = []; // История состояний страницы
//
// // Функция сохранения состояния страницы
// function savePageState() {
//     const state = {
//         productsHTML: productsSection.innerHTML,
//         paginationDisplay: pagination.style.display,
//         diceContainerDisplay: diceContainer.style.display,
//         backButtonDisplay: backButton.style.display,
//         allCategoriesButtonDisplay: allCategoriesButton.style.display,
//         searchInputValue: searchInput.value.trim()
//     };
//     pageStateHistory.push(state); // Сохраняем текущее состояние в историю
// }
//
// // Функция восстановления состояния страницы
// function restorePageState(state) {
//     if (state) {
//         productsSection.innerHTML = state.productsHTML;
//         pagination.style.display = state.paginationDisplay;
//         diceContainer.style.display = state.diceContainerDisplay;
//         allCategoriesButton.style.display = state.allCategoriesButtonDisplay;
//         backButton.style.display = state.backButtonDisplay;
//         searchInput.value = state.searchInputValue;
//     }
// }