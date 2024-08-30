$(document).ready(function() {
    $('.textSearch-actions').click(function() {
       var searchTerm = $('#searchInputLabel').val();

            $.ajax({
                url: '/content/anf-code-challenge/us/en.search.json?searchTerm='+searchTerm, 
                type: 'GET',
                success: function(response) {
                    $('#searchResults').empty(); // Clear any previous results
                    var results = response;

                    if (results.length === 0 || results[0].message) {
                        $('#searchResults').append('<p>No results found for your search term.</p>');
                    } else {
                        $.each(results, function(index, result) {
                            var resultHTML = '<div class="result-item">' +
                                             '<h3>' + result.title + '</h3>' +
                                             '<p>' + result.description + '</p>';
                            if (result.image) {
                                resultHTML += '<img src="' + result.image + '" alt="' + result.title + '">';
                            }
                            resultHTML += '<p><strong>Last Modified:</strong> ' + result.lastModified + '</p>' +
                                          '</div><hr>';
                            $('#searchResults').append(resultHTML);
                        });
                    }
                },
                error: function() {
                    $('#searchResults').html('<p>An error occurred while processing your request. Please try again later.</p>');
                }
            });
        });
    });
