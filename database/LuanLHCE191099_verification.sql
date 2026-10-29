USE [A2LuanLH_SE1910];
GO
SELECT AccountID, AccountName, AccountEmail, AccountRole FROM SystemAccount;
SELECT CategoryID, CategoryName, ParentCategoryID, IsActive FROM Category;
SELECT n.NewsArticleID, n.NewsTitle, n.NewsStatus, c.CategoryName, a.AccountName AS Author
FROM NewsArticle n JOIN Category c ON n.CategoryID=c.CategoryID JOIN SystemAccount a ON a.AccountID=n.CreatedByID;
SELECT nt.NewsArticleID, t.TagID, t.TagName, t.Note FROM NewsTag nt JOIN Tag t ON nt.TagID=t.TagID;
