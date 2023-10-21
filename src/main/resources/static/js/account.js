
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


//js for checking the edit profile validation

function validateName(input) {

    var lettersPattern = /^[A-Za-z]+$/;
    var inputValue = input.value;
    if (!lettersPattern.test(inputValue)) {
       input.value = "";
              Swal.fire({
                  icon: 'error',
                  title: 'Validation Error',
                  text: 'Full Name should contain letters only. No spaces are allowed'
              });
    }
}


function validateNumber(input) {
    // Define a regular expression pattern to match exactly 10 digits.
   var numberPattern = /^\d*$/;

    // Get the input value.
    var inputValue = input.value;

    // Check if the input matches the pattern.
    if (!numberPattern.test(inputValue)) {
        // If it doesn't match, clear the input and show a SweetAlert.
        input.value = "";
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'no letters or special characters.'
        });
    }
}