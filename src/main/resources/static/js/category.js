    $(document).ready(function () {
	loadDataTable();
	
	});
	
	function loadDataTable(status) {
    dataTable = $('#tblData').DataTable({
        "ajax": {
            "url": "/Order/GetAll?status=" + status
        },
        "columns": [
            { "data": "id", "width": "5%" },
            { "data": "name", "width": "25%" },
            { "data": "description", "width": "15%" },
           
            {
                "data": "id",
                "render": function (i) {
                    return `<div class="w-75 btn-group" role="group">
                                <a href="/cate/Details?orderId=${data}" class="btn btn-outline-dark">Edit/Details</a>
                            </div>`
                },
                "width": "15%"
            }
        ]
    });
}