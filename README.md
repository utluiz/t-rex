T-Rex
===========

> The (best) Report Excel generator!

With T-Rex you can easily generate spreadsheets based upon templates and a model.

# Motivation

Spreadsheets are uses all around the corporate world. Developers routinely need to code using libs like Apache POI in order to create these files in the format required by the user.

The problem is: this is not as easy or intuitive as it looks like. 

T-Rex role is to abstract the inner workings of POI and sheet manipulation in the same fashion as many template technologies do.

Nobody outputs HTML using *print* methods (at least they shoudn't). We have JSP, Velocity and many other for that! 

So, why we're still hard coding row and cell positions? Why not let the user creates his own templates with a well defined and powerful markup language and a documented model?

That's what T-Rex is all about. It can save you hours of boring programming and of changes every time the user wants to add a cell in the template. ;)    

# Hello World

# Requirements

If you use Maven, just add the dependency tag to your `pom.xml`. 

Otherwise you'll need to manually add to your classpath POI dependencies (`poi` and `poi-ooxml`) and MVEL2 dependencies.

T-Rex project has two dependencies:

# Main Concepts

# Features

## MVEL2

It's a powerful expression language interpreter used in JBoss projects.

## Apache POI

It's the best free library to work with Microsoft Office documents.

# Features

## Powerful expression language

MVEL2 provides a complete set of features like conditionals statements (if, ternary), loops, variables, functions. You'll be able to call any method from your model and manipulate values.

For a complete reference of this language, please refer to MVEL2 User Guide.

Notice that `@for` and `@if` special markup are not from MVEL, even though they delegate the execution to it.

## Automatic row replication

T-Rex can *automagically* create and fill rows based on a model list. Just put a list in the model and use `@for` statement in any cell of the row you can replicate.

Example:

    @for(item : list)
    
You can also replicate a range of rows:

    @for(item : list) 3 rows
    
In the first example T-Rex will clone the row where is `@for` using the size of the list and then iterate over the items processing the respective row.

The second example is analogous, except that it will replace the row containing the `@for` and the two rows below. If you have 4 items in the list, there will be 12 rows (9 new) in the final sheet.

## Additional changes

Every library in the world has a limited scope and cannot meet every requirement. It's awful when you need just to tweak it a little but you have no extension points, no listeners, no callbacks.

Well, more than extending T-Rex classes (see how to do it a few topics below), you can do additional modifications to your template implementing `AdditionalModifications` interface ans configuring T-Rex to use it.

Yout callback methods will be called before and after T-Rex interprets the sheet, so you are able to do your custom stuff. 

## I18n (Internationalization)

Just add a ResourceBundle to the model. Example:

    ResourceBundle resourceBundle = ResourceBundle.getBundle("com.application.msg", currentLocale);
    Trex
        .template().load(templateFile)
        .model().add("i18n", resourceBundle)
        .output().to(outputFile);

## L10n (Localization)

You can change the cell format for dates and numbers through the provided functions `format.date` and `format.number`.

For example:

    @{format.number(model.myNumberAttribute, "0.00")} 
    @{format.date(model.myDateAttribute, "dd/mm/yy")}
    
`format` is a special class provided by T-Rex. It will store the pattern informed in the second parameter as MVEL2 interprets the expression and apply it later to the cell.

# Extensibility

All T-Rex components were designed to be extensible.

For instance, you can write your own `SheetParser` based on another library instead of POI. The you just configure T-Rex to use it.

You can also write your on expression interpreter. Just create an implementaiton of `Interpreter` interface.

# Roadmap

These features are planned to be included in the next releases, as I have time to work on them.

- **Horizontal loop**: iterate over columns. Maybe something like `@hfor`. It'll be needed to implement a somewhat complex column shift method.
- **Partial loop**: iterate over some cells of a row or of a column. Depends on a generic method to move cells.
- **`SheetParser` alternative implementations**: provide a way to generate using Aspose or jExcel.
