function saveOrder() {
    var paymentStatus = document.getElementById("paymentMethod").textContent;
    var addressId = document.getElementById("addressId").textContent;
    console.log(addressId+"addressId=");
    const data = {
        paymentStatus: paymentStatus,
        addressId: addressId

    };

    // Send an AJAX POST request to the backend endpoint
    $.ajax({
        type: "POST",
        url: "/saveOrder",
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (response) {


                        window.location.href = "/orderSuccess";

                        console.log("Order was not saved successfully.");


        },
        error: function (xhr, status, error) {
            console.log("Error occurred while saving the order:", error);

        }
    });
}





const buy = document.querySelector("#buy");

const total_amount_AfterDiscount = document.getElementById("priceAfterDiscount").textContent;

const offerPrice = document.getElementById("offerPrice").textContent;

var total = 0;

if(total_amount_AfterDiscount != ""){
     total = total_amount_AfterDiscount;

}
else{
     total = offerPrice;

}


buy.addEventListener("click", () => {


  const paymentStatus = document.getElementById("paymentMethod").textContent;
  console.log(paymentStatus + " hello")
  if (paymentStatus === "ONLINE") {
    $.ajax({
      type: "POST",
      url: "/payment/checkout",
      data: total,
      success: function (response) {
        console.log(response);
        const orderId = response
        // Update relevant elements in the Thymeleaf template
        $('#successMessage').text(response);
        fetch('/payment/confirm', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ orderId: orderId })
                    })
                    .then(function (response) {
                        return response.json();
                    })
                    .then(function (response) {
                        var options = {
                            key: response.key,
                            currency: 'INR',
                            amount: response.amount,
                            order_id: response.id,
                            name: 'Pedal Planet',
                            description: 'Online Payment',
                            handler: function (response) {

                            console.log(response)
                                saveOrder();

                            },


                            prefill: {
                                name:  response.customerName,
                                email: response.customerEmail,
                                contact: response.customerContact,
                            }
                        };

                        var rzp1 = new Razorpay(options);
                        rzp1.open();
                    });
      },
      error: function (xhr, status, error) {
        console.log(error);
        // Handle error case if needed
      },
      contentType: "text/plain"
    });
  } else if (paymentStatus === "COD") {
    saveOrder();
    console.log("payment cash on delivery order")
  }else if (paymentStatus === "WALLET"){
  console.log("payment wallet order")
   walletPayment();
  }
});

function walletPayment() {
    var paymentStatus = document.getElementById("paymentMethod").textContent;
    var addressId = document.getElementById("addressId").textContent;
    console.log("addressId=" + addressId);

    const data = {
        paymentStatus: paymentStatus,
        addressId: addressId
    };

    $.ajax({
        type: "POST",
        url: "/walletPayment",
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (response) {
            if (response === "Payment Successful") {
                // Payment was successful, redirect to order success page
                window.location.href = "/orderSuccess";
            } else {
                // Handle any other success scenario here
                console.log("Order was not saved successfully.");
            }
        },
        error: function (xhr, status, error) {
            console.log("Error occurred while saving the order:", error);

            // Display SweetAlert for Insufficient Balance
            if (xhr.status === 400 && xhr.responseText === "Insufficient Balance") {
                Swal.fire({
                    icon: 'error',
                    title: 'Insufficient Balance',
                    text: 'Your wallet balance is too low to complete this order.',
                });
            }
        }
    });
}

function validatePhoneNumber(input) {
    var phoneNumber = input.value;

    // Use a regular expression to check for non-digit characters.
    var nonDigitPattern = /\D/;

    if (nonDigitPattern.test(phoneNumber)) {
        // If non-digit characters are found, clear the input and display a SweetAlert.
        input.value = "";
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'No letters or special characters'
        });
    }
}

