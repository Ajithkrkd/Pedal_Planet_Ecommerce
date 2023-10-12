
function hideCartErrorDiv()
    {
        var cartErrorDiv = document.getElementById('success_password');
        if(cartErrorDiv)
        {
            cartErrorDiv.style.display = 'none';
        }
    }
    setTimeout(hideCartErrorDiv ,6000)

    function passwordMessageDiv()
    {
        var passwordId = document.getElementById('error_password');
        if(passwordId){
         passwordId.style.display='none';

        }
    }
    setTimeout(passwordMessageDiv,6000);



        function setDefaultAddress(radioInput) {

            var addressId = $(radioInput).val();


            $.ajax({
                url: '/account/set_default_address/' + addressId,
                type: 'POST',
                success: function(data) {

                    console.log('Default address updated successfully');
                     Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: 'Default address updated successfully',
                        });
                },
                error: function(error) {

                    console.error('Error updating default address: ' + error);
                }
            });
        }
