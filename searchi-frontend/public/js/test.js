$( document ).ready(function() { 
    pageSize = 1;
    pagesCount = $(".content").length;
    var currentPage = 1;
    
    var nav = '';
    var totalPages = Math.ceil(pagesCount / pageSize);
    for (var s=0; s<totalPages; s++){
        nav += '<li class="pageNumbers"><a href="#">'+(s+1)+'</a></li>';
    }
    $(".pagePrev").after(nav);
    $(".pageNumbers").first().addClass("active");
    
    showPage = function() {
        $(".content").hide().each(function(n) {
            if (n >= pageSize * (currentPage - 1) && n < pageSize * currentPage)
                $(this).show();
        });
    }
    showPage();


    $(".pagination li.pageNumbers").click(function() {
        $(".pagination li").removeClass("active");
        $(this).addClass("active");
        currentPage = parseInt($(this).text());
        showPage();
    });

    $(".pagination li.pagePrev").click(function() {
        if($(this).next().is('.active')) return;
        $('.pageNumbers.active').removeClass('active').prev().addClass('active');
        currentPage = currentPage > 1 ? (currentPage - 1) : 1;
        showPage();
    });

    $(".pagination li.pageNext").click(function() {
        if($(this).prev().is('.active')) return;
        $('.pageNumbers.active').removeClass('active').next().addClass('active');
        currentPage = currentPage < totalPages ? (currentPage+1) : totalPages;
        showPage();
    });
});