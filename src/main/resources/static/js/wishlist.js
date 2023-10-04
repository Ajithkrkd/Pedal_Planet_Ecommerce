document.addEventListener("DOMContentLoaded", function () {
const wishlistButtons = document.querySelectorAll('button[data-product-id]');

    function updateButtonState(productId) {
        fetch(`/wishList/check/${productId}`, {
            method: 'GET',
        })
        .then((response) => response.json())
        .then((response) => {
            const button = document.querySelector(`button[data-product-id="${productId}"]`);

            if (response.inWishlist) {
                button.textContent = 'WISHLISTED';
                button.classList.remove('btn-secondary');
                button.classList.add('btn-success');
                button.disabled = true;
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    }

    wishlistButtons.forEach((button) => {
        button.addEventListener('click', function () {
            const productId = this.getAttribute('data-product-id');

            fetch(`/wishList/add/${productId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            })
            .then((response) => response.json())
            .then((response) => {
                if (response.error) {
                     Swal.fire({
                                     icon: 'error',
                                     title: 'Error',
                                     text: response.error,
                                   });
                } else {

                    this.textContent = 'WISHLISTED';
                    this.classList.remove('btn-secondary');
                    this.classList.add('btn-success');
                    this.disabled = true;
                }


                updateButtonState(productId);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        });

        // Check and update button state when the page loads
        const productId = button.getAttribute('data-product-id');
        updateButtonState(productId);
    });
});

 document.addEventListener("DOMContentLoaded", function () {
     const removeButtons = document.querySelectorAll('.remove-button');

     removeButtons.forEach((button) => {
         button.addEventListener('click', function () {
             const productId = this.getAttribute('data-product-id2');

             fetch(`/wishList/remove/${productId}`, {
                 method: 'DELETE',
                 headers: {
                     'Content-Type': 'application/json',
                 },
             })
             .then((response) => {
                             if (response.ok) {
                                 console.log(response);
                                const productElement = this.closest('.productElement'); // Adjust the selector as needed
                                 if (productElement) {
                                 productElement.remove();
                                 }

                             }
                             else {
                                 console.log("Error: " + response.status);
                             }
                         })
                         .catch((error) => {
                             console.error('Error:', error);
             });
         });
     });
 });

