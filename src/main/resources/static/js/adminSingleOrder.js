console.log("entered");

function updateStatus(button) {
    const newStatus = button.getAttribute("data-status");
    const orderId = button.getAttribute("data-order-id");
    console.log(orderId + " updated id");
    console.log(newStatus +"    updated status");

    $.ajax({
        url: `/admin/update_status/${newStatus}/${orderId}`,
        type: "POST",
        success: function (response) {
            // Update the status in the frontend
            const statusElement = $(button).closest(".cancelbtn");
            statusElement.removeClass(`status-${response.oldStatus}`);
            statusElement.addClass(`status-${response.newStatus}`);
        },
        error: function () {
            alert("Error updating status.");
        }
    });
}





    document.addEventListener('DOMContentLoaded', function () {
        const approveButtons = document.querySelectorAll('.approve-button');

        approveButtons.forEach(button => {
            button.addEventListener('click', function () {
                const orderId = this.getAttribute('data-order-id');
                const url = `/admin/approve_return_request/${orderId}`;

                fetch(url, {
                    method: 'POST',

                })
                .then(response => {
                   console.log(response)
                   window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                });
            });
        });
    });

