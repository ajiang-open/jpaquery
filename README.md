# 框架优势

## 类型安全

字符串只能和字符串比较，数字只能和数字比较。

## 防SQL注入

基本不存在将参数直接拼接进查询语句的可能，杜绝SQL注入风险。

## 重构支持

可以直接重构实体的getter方法，则所有位置都将一起重构，对于修改字段信息是非常友好的，而且能够及时发现字段修改不到位的错误。

## 提示友好

再也不必到处去查实体包含哪些字段，也不再担心写错字段名，IDE直接提示。

## 简单易用

与其它类似框架，如QueryDSL，Hibernate Criteria等相比，更像SQL，更容易理解和使用。

# 使用手册

请参考[JpaQuery手册](https://github.com/ajiang-open/jpaquery/wiki/JpaQuery-Manual)