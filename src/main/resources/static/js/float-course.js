(function () {
    function itemTemplate(suggestion) {
        return [
            "<div " + (suggestion._item.floated ? "class='bg-danger'" : "") + ">",
            "<span class='code'>", fuzzyhound.get().highlight(suggestion.code, "code"), " - </span>",
            "<span class='title'><i>", fuzzyhound.get().highlight(suggestion.title,"title") ,"</i></span> ",
            "<span class='category capsule'>", suggestion._item.category, "</span> ",
            "<span class='semester'>Sem: ", suggestion._item.semester, " - </span>",
            "<span class='credits'>Credits: ", suggestion._item.credits, "</span>",
            (suggestion._item.floated ? "  <span class='capsule category'>Floated</span>" : ""),
            "</div>"
        ].join("");
    }

    function courseTemplate(item) {
        return [
            '<div class="course card">',
            '<input type="text" class="chosen-code" name="code" value="' + item.code + '" hidden>',
            '<div class="card-body">',
            '<div class="card-block">',
            '<h4 class="card-title"><strong>' + item.code + '</strong> - ' + item.title + '</h4>',
            '<p class="card-text">',
            '<span class="capsule category">' + item._item.category + '</span>  ',
            '<span class="semester">Semester : ' + item._item.semester + '</span> - ',
            '<span class="credits">Credits : ' + item._item.credits + '</span></p>',
            '<button class="remove btn btn-outline-danger float-xs-right">Remove</button>',
            '</div></div></div>'
        ].join('');
    }

    var added = [];

    function calculateAdded() {
        added = [];
        $('.chosen-code').each(function () {
            added.push($(this).val());
        });
    }

    function attachRemove() {
        $('.remove').click(function (event) {
            $(event.target).closest('.course').remove();
            calculateAdded();
            if (added.length === 0)
                $('.selected-courses-container').prop('hidden', true);
        });
    }

    function createList(courses) {
        var list = $('#course-selection-list');
        list.html('');
        $.each(courses, function (index, course) {
            if (course.floated || (added.indexOf(course.code) !== -1))
                return;
            list.append("<li style='padding: 5px;'><input name='to-float' type='checkbox' style='margin-right: 10px; vertical-align: middle'><strong id='c-code'>"+course.code+"</strong> - "+course.title+" - <span class='capsule'>Semester: " + course.semester + "</span></li>");
        });
    }

    function addCourse(item) {
        if (item._item.floated) {
            toastr.error('Course is already floated!');
            return;
        }

        if (added.indexOf(item.code) !== -1) {
            toastr.error('Already added!');
            return;
        }

        var container = $('.selected-courses-container');
        container.prop('hidden', false);
        var list = $('.selected-courses');
        list.append(courseTemplate(item));

        calculateAdded();
        attachRemove();
    }

    var department = $('#department').html();
    var courses = null;

    fuzzyhound.onLoad(function (response) {
        courses = response;
    });

    fuzzyhound.setSource("/department/" + department + "/api/courses", ["code", "title"]);

    $(document).ready(function () {
        var searchBox = $('.courses');
        searchBox.typeahead({
            highlight: false,
            minLength: 2
        }, {
            name: 'course',
            limit: 100,
            source: fuzzyhound.get(),
            display: "item.title",
            templates: {
                suggestion: itemTemplate
            }
        }).on('typeahead:select', function (ev, suggestion) {
            addCourse(suggestion);
        });

        attachRemove();

        $('#select-courses').click(function () {
            createList(courses);
        });

        $('#float-selected').click(function () {
            $("input[name='to-float']:checked").parent().find('#c-code').each(function () {
                var courseCode = this.innerHTML;
                addCourse($.grep(courses, function (item) {
                    return item.code === courseCode;
                }).map(function (item) {
                    return {
                        _item: item,
                        code: item.code,
                        title: item.title
                    }
                })[0]);
            });
        });
    });
}());