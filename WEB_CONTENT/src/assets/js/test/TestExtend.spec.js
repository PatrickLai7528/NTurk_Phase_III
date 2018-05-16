import extend from '../Extend.js'

describe('Test for function extend', function () {
    // Class Person
    function Person(name) {
        this.name = name;
    }

    Person.prototype.getName = function () {
        return this.name;
    }

    // Class Author
    function Author(name, books) {
        Person.call(this, name);
        this.books = books;
    }

    extend(Author, Person);

    Author.prototype.getBooks=function(){
        return this.books;
    }
    it('',function(){
        var author = new Author('Patrick','Happy Life');
        expect(author.getBooks()).toBe('Happy Life');
        expect(author.getName()).toBe('Patrick');
        expect(author.__proto__).toBe(Author.prototype);
    })
})