
  function sendCartItem(variantId) {

     $.ajax({
         type: "POST",
         url: "/cart/increase",
         data: variantId,
         success: function (response) {

             const quantity = document.getElementById(`inputId-${variantId}`)
             const priceTagOriginal = document.getElementById(`priceTagOriginal-${variantId}`)
             const actualPrice = document.getElementById(`actualPrice-${variantId}`)
             const priceTotal = document.getElementById("priceTotal");
             const offerPriceTotal = document.getElementById("offerPriceTotal")
             const bagLimitText = document.getElementById(`bagLimitText-${variantId}`);
             const content = "Your Bag limit is full";
             const content2 = "";
             if ("newQuantity" in response) {
                console.log(response)
                 var newQuantity = response.newQuantity;
                const stockValue = response[`variantStock-${variantId}`];



                if(newQuantity >= stockValue ) {
                    bagLimitText.textContent = content;

                }

                 const offerPriceforTotal = response.total_Offer_price;
                 const priceforTotal = response.total_price;

                 const price = response.price;
                 const offerPrice = response.offerPrice;



                 var id = response.variantId;
                 quantity.textContent = newQuantity
                 priceTotal.textContent = priceforTotal ;
                 offerPriceTotal.textContent = offerPriceforTotal ;
                        priceTagOriginal.textContent = offerPrice * quantity.textContent;
                        actualPrice.textContent = price * quantity.textContent ;


             } else {

                 console.error("Error: " + response.error);
             }
         },
         error: function (xhr, status, error) {
             console.log(error);
             // Handle error case if needed
              const bagLimitText = document.getElementById(`bagLimitText-${variantId}`);
              bagLimitText.textContent = " reached stock limit ";// Store the error message
         },
         contentType: "text/plain"
     });
  }


 $(document).ready(function () {
   var errorMessage = sessionStorage.getItem('errorMessage');
   if (errorMessage) {
     $('#errorMessage').text(errorMessage).show(); // Display the stored error message
     sessionStorage.removeItem('errorMessage'); // Remove the error message from storage
   }
 });

 function sendCartItemDecr(variantId) {
    const quantity = document.getElementById(`inputId-${variantId}`)
    console.log(quantity.textContent)
     if( quantity.textContent === '1'){
    alert("Last item can't delete you can use REMOVE button");
   }else{

   $.ajax({
           type: "POST",
           url: "/cart/decrease",
           data: variantId,
           success: function(response) {

             const quantity = document.getElementById(`inputId-${variantId}`)
             const priceTagOriginal = document.getElementById(`priceTagOriginal-${variantId}`)
             const actualPrice = document.getElementById(`actualPrice-${variantId}`)



             const priceTotal = document.getElementById("priceTotal");
             const offerPriceTotal = document.getElementById("offerPriceTotal")
             const bagLimitText = document.getElementById(`bagLimitText-${variantId}`);
             const content2 = "";




          if ("newQuantity" in response) {
               const id = response.variantId;
               var newQuantity = response.newQuantity;
                const stockValue = response[`variantStock-${variantId}`];
                console.log(stockValue + "ajith")
               if(newQuantity < stockValue  ){
                                   bagLimitText.textContent = content2;

                               }
               const offerPriceforTotal = response.total_Offer_price;
               const priceforTotal = response.total_price;
               const price = response.price;
               const offerPrice = response.offerPrice;
               quantity.textContent = newQuantity
                 priceTagOriginal.textContent = offerPrice * quantity.textContent ;
                 actualPrice.textContent = price * quantity.textContent ;
                 priceTotal.textContent = priceforTotal;
                 offerPriceTotal.textContent = offerPriceforTotal;
               }


               else {

                    console.error("Error: " + response.error);
                }
            },
            error: function (xhr, status, error) {
                console.log(error);
                // Handle error case if needed
                 const bagLimitText = document.getElementById(`bagLimitText-${variantId}`);
                              bagLimitText.textContent = "Can't decrease "; // Store the error message
            },
            contentType: "text/plain"
        });


   }




  }
 function deleteCartItem(variantId, quantity) {
   console.log(variantId);
   console.log(quantity);

   var requestData = {
     variantId: variantId,
     quantity: quantity
   };

   $.ajax({
     type: "POST",
     url: "/cart/delete",
     data: JSON.stringify(requestData),
     success: function (response) {
       console.log(response);
       // Update relevant elements in the Thymeleaf template
       $('#successMessage').text(response);
       location.reload();
     },
     error: function (xhr, status, error) {
       console.log(error);
       // Handle error case if needed
     },
     contentType: "application/json"
   });
 }







function applyCoupon() {
    var couponCode = document.getElementById("couponCodeInput").value;
    var errorMessage = document.getElementById("errorMessage")
    if (couponCode === "") {
        // Clear previous messages and display an error
        successMessage.innerText = "";
        price_after_discount.innerText = " ";
        errorMessage.innerText = "Please enter a coupon code.";
    }
    else {
        errorMessage.innerText = "";

        // Perform an AJAX request to apply the coupon
        $.ajax({
            type: "POST",
            url: "/cart/apply-coupon?couponCode=" + couponCode,
            data: couponCode,
            success: function (response) {
                // Display the coupon availed message and discount percentage if the coupon is valid
                if (response.valid) {
                    var successMessage = document.getElementById("successMessage");
                    var priceAfterDiscount = document.getElementById("price_after_discount");
                    var errorMessage = document.getElementById("errorMessage");
                    var discountTextId = document.getElementById("discount_amount");

                    var discountPercentage = response.discountPercentage;

                    var discount_price = response.discount_amount;

                    var message = ""; // Initialize the message variable

                    // Check the coupon type and display the appropriate message
                    if (response.productSpecific && response.coupon_type === "PRODUCT") {
                        message = "Coupon availed! You got " + discountPercentage + "% off on this product.";
                        priceAfterDiscount.innerText = `${response.price_after_discount}`;
                        discountTextId.innerText = `${response.discount_amount}`;
                    } else if (response.coupon_type === "PRODUCT") {
                        message = "Coupon not applicable to this product.";
                    }

                    if (response.categorySpecific && response.coupon_type === "CATEGORY") {
                        message = "Coupon availed! You got " + discountPercentage + "% off on this category.";
                        priceAfterDiscount.innerText = `${response.price_after_discount}`;
                        discountTextId.innerText = `${response.discount_amount}`;
                    } else if (response.coupon_type === "CATEGORY") {
                        message = "Coupon not applicable to this category.";
                    }

                    // Handle general coupons
                    if (response.coupon_type === "GENERAL") {
                        priceAfterDiscount.innerText = `${response.price_after_discount}`;
                        message = "Coupon availed! You got " + discountPercentage + "% off.";
                            discountTextId.innerText = `${response.discount_amount}`;
                            const couponAddForm = document.getElementById("coupon_add_form");
                            const couponCloseForm = document.getElementById("coupon_close_form");
                            couponAddForm.style.display = "none";
                            couponCloseForm.style.display = "block";

                    }

                    successMessage.innerText = message;
                }
                else {
                    console.log("Error getting")

                    const errorMessage = document.getElementById("errorMessage");
                    errorMessage.innerText = response.error_message;
                }

            },
            // Handle AJAX errors (you might want to display an error message).
            error: function () {

                errorMessage.innerText = "Enter a valid coupon";
            }
        });
    }

}
