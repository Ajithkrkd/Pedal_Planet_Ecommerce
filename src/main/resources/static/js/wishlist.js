document.addEventListener("DOMContentLoaded", function () {
const wishlistButtons = document.querySelectorAll('button[data-product-id]');

    function updateButtonState(productId) {
        fetch(`/wishList/check/${productId}`, {
            method: 'GET',
        })
        .then((response) => response.json())
        .then((response) => {
        console.log(response)
        console.log(response)
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

$(document).on("click", ".add-to-cart", function() {
    // Get the product ID associated with the clicked button
    var productId = $(this).data("product-id");




    // Send an AJAX request to the backend to retrieve variants
    $.ajax({
        url: "/wishList/findVariants", // Replace with your actual backend endpoint
        method: "POST",
        data: { productId: productId },
        success: function(response) {
            console.log(response)
            displayVariantSelectionModal(response);
        },
        error: function(error) {
            // Handle any errors from the backend
            console.error("Error retrieving variants:", error);
        }
    });
});

function displayVariantSelectionModal(variants) {
    // Create an HTML string for the variant options
    var variantOptionsHtml = '';

    // Iterate through the variants and generate option elements
    for (var i = 0; i < variants.length; i++) {
        var variant = variants[i];
        // Split the variant data into ID and name
        var [variantId, variantName] = variant.split(',');
        variantOptionsHtml += `<option value="${variantId}" data-variant-name="${variantName}">${variantName}</option>`;
    }

    // Build the HTML for the modal content
    var modalContent = `
        <!-- Modal Header -->
        <div class="modal-header">
            <h5 class="modal-title">Select Variant</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <!-- Modal Body -->
        <div class="modal-body">
            <form id="addToCartForm" action="/wishList/getVariantAndAddProductToCart" method="post">
                <select id="variantSelect" name="variantId" class="form-control">
                    ${variantOptionsHtml}
                </select>
                <button type="submit" class="btn btn-primary mt-2">Add to Cart</button>
            </form>
        </div>
        <!-- Modal Footer -->
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        </div>
    `;

    // Set the modal content
    $("#addToCartModal .modal-content").html(modalContent);

    // Show the modal
    $("#addToCartModal").modal("show");
}



/*
$(document).on("click", "#addToCartButton", function() {
    var selectedOption = $("#variantSelect option:selected");
    var variantId = selectedOption.val();
    var variantName = selectedOption.data("variant-name");

    console.log("Selected Variant ID: " + variantId);
    console.log("Selected Variant Name: " + variantName);

    $.ajax({
        url: "/wishList/getVariantAndAddProductToCart",
        method: "POST",
        data: { variantId: variantId },
        success: function(response) {
            console.log(response, "Success");
            if (response.includes("successfully")) {
                window.location.href = "/wishList?success";
            } else {
                window.location.href = "/wishList?error";
                console.error("Error: " + response);
            }
        },
        error: function(error) {
            console.log("AJAX request failed:", error);
        }
    });
});*/

