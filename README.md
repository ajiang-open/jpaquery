# 新建Query对象
```java
JpaQuery jpaQuery = Querys.newJpaQuery();
```
# 指定要查询的实体类
```java
User modelUser =  jpaQuery.from(User.class);//从一个实体类中查询，返回对象是一个代理的实体实例，用于监听后续的用户操作。
```
翻译之后的JPQL是：
```sql
from User 
```
# And查询
```java
jpaQuery.where(modelUser.getUserName()).equal("张三");
jpaQuery.where(modelUser.getAge()).greatEqual(30);
```
翻译之后的JPQL是：
```sql
 where user.userName = '张三' and user.age >= 30 
```
# Or查询
```java
Or or = jpaQuery.where().or();
or.get(XXXX).equal(XXXX);
or.get(XXXX).like(XXXX);
```
翻译之后的JPQL是：
```sql
 (xxxx = xxxx or xxxx like xxxx) 
```
# And和Or嵌套
类似于Or，And也可以单独抽出来，如下：
```java
And and = jpaQuery.where().and();
and.get(XXXX).equal(XXXX);
and.get(XXXX).like(XXXX);
```
翻译之后的JPQL是：
```sql
 (xxxx = xxxx and xxxx like xxxx) 
```
同时，Or里面也可以And，如：
```java
Or or = jpaQuery.where().or();
or.get(XXXX).equal(XXXX);
…,
or.get(XXXX).like(XXXX);

And and = or.and();
and.get(XXXX).equal(XXXX);
and.get(XXXX).like(XXXX);
```
翻译之后的JPQL是：
```sql
 (xxxx = xxxx or xxxx like xxxx and (xxxx = xxxx and xxxx like xxxx)) 
```
反之And里面也可以继续Or，从而实现无限嵌套的操作。
# 参数非空才匹配模式（省略if判断）
```java
jpaQuery.where(modelUser.getUserName()).equalIfExists(a);//当a有值时才加入此条件
```
或者
```java
and.get(modelUser.getUserName()).likeIfExists(a);//当a有值时才加入此条件
```
# 关联实体字段查询
```java
jpaQuery.where(modelUser.getCompany().getComcode()).equal("000000");//相当于 user.company.comcode = '000000'
```
翻译之后的JPQL是：
```sql
 user.company.comcode = '000000' 
```

# 多实体类查询
```java
Company modelCompany = jpaQuery.from(Company.class);
```
翻译之后的JPQL是：
```sql
 from User,Company 
```
# 实体字段间相互匹配
```java
jpaQuery.where(modelUser.getCompany().getName()).equal(modelCompany.getName());
```
翻译之后的JPQL是：
```sql
 user.company.name = company.name 
```
# 左右Like查询
```java
String code = "00";
jpaQuery.where(modelUser.getCompany().getName()).likeLeft(modelCompany.getName());
jpaQuery.where(modelUser.getCompany().getName()).likeRightIfExists(code);
```
翻译之后的JPQL是：
```sql
user.company.name like concat(company.name,'%') and user.company.name like '%00'
```
# 执行查询及结果
在Spring管理的任意Bean中，如果JPA环境有效，则可以通过如下方式获取EntityManager实例：
```java
@PersistenceContext
EntityManager em;
```
然后，可直接通过EntityManager对象来执行查询操作，如下（这里是假设还没有执行from(Company.class)的单实体查询情况）：
```java
List<User> users = (List<User>)jpaQuery.list(em);
```
对于多实体查询，则结果如下：
```java
JpaQuery jpaQuery = Querys.newJpaQuery();
// 指定要查询的实体类型
User modelUser =  jpaQuery.from(User.class);
Company modelCompany = jpaQuery.from(Company.class);
// 执行查询操作，返回一个List
List<Object[]> objs = (List<Object[]>)jpaQuery.list(em);
// List中的每一条记录是一个数组
Object[] rowObject = objs.get(0);
// 这个数组的第一个元素是第一张表的实体，第二个元素是第二张表的实体
User user = (User)rowObject[0];
Company company = (Company)rowObject[1];
```
# 限定结果集
查询前5行记录：
```java
JpaQuery jpaQuery = Querys.newJpaQuery();
// 指定要查询的实体类型
User modelUser =  jpaQuery.from(User.class);
List<User> users = jpaQuery.top(em,5);
```
分页查询：
```java
JpaQuery jpaQuery = Querys.newJpaQuery();
// 指定要查询的实体类型
User modelUser =  jpaQuery.from(User.class);
// 查询第一页数据（页数从0开始），每页10条记录
Page<User> userPage = jpaQuery.page(em,0,10);
```
# 查询Select子句
以上例子中，都没有提及当我只查询一个字段或者几个字段，以及聚合函数用法等等，这里举几个例子。
## 普通查询
```java
JpaQuery jpaQuery = Querys.newJpaQuery();
// 指定要查询的实体类型
User modelUser =  jpaQuery.from(User.class);
// 只查询名字和年龄
jpaQuery.select(modelUser.getName());
jpaQuery.select(modelUser.getAge());
// 执行查询操作，返回一个List
List<Object[]> objs = (List<Object[]>)jpaQuery.list(em);
// List中的每一条记录是一个数组
Object[] rowObject = objs.get(0);
// 这个数组的第一个元素是名字，第二个元素是年龄
String name = (String)rowObject[0];
Integer age = (Integer)rowObject[1];
```
翻译后的JPQL如下：
```sql
select name,age from User......
```
## 聚合函数查询
distinct查询：
```java
jpaQuery.select(modelUser.getName()).distinct();
```
翻译后的JPQL如下：
```sql
select distinct name from User......
```
sum查询：
```java
jpaQuery.select(modelUser.getAge()).sum();
```
翻译后的JPQL如下：
```sql
select sum(age) from User......
```
# 子查询
## in查询
```java
JpaQuery subJpaQuery = jpaQuery.subJpaQuery();

School modelSchool = subJpaQuery.from(School.class);

subJpaQuery.where(modelSchool.getName()).like("%市立中学");

subJpaQuery.select(modelSchool.getSchoolCode());

jpaQuery.where(modelUser.getSchoolCode()).in(subJpaQuery);
```
翻译后的JPQL如下：
```sql
where user.schoolCode in (select school.schoolCode from School school where school.name like '%市立中学' ) 
```
## exists查询
也可以利用子查询做存在查询
```java
jpaQuery.where().exists(subJpaQuery);//存在查询
```
## any、some、all查询
还可以做HQL中特有的谓词子查询
```java
jpaQuery.where(modelUser.getSchoolCode()).equal(subJpaQuery.any());
jpaQuery.where(modelUser.getSchoolCode()).equal(subJpaQuery.some());
jpaQuery.where(modelUser.getSchoolCode()).equal(subJpaQuery.all());
```
分别相当于：
```sql
where user.schoolCode = any (select school.schoolCode from School school where school.name like '%市立中学' ) 
where user.schoolCode = some (select school.schoolCode from School school where school.name like '%市立中学' ) 
where user.schoolCode = all (select school.schoolCode from School school where school.name like '%市立中学' ) 
```
# 结果排序
```java
jpaQuery.order(user.getUserName()).desc();
jpaQuery.order(user.getAge()).asc();
```
翻译后的JPQL如下：
```sql
order by user.userName desc,user.age asc
```
# 内连接、左连接和右连接
```java
User modelMateLeft =jpaQuery.join(user.getMates()).left();//和用户表中的同学集合左连接
```
翻译后的JPQL如下：
```sql
from User user left join user.metas mate
```
在左连接上加上on限定：
```java
jpaQuery.on(modelMateLeft)
.get(modelMateLeft.getSchoolCode())
.equal(modelUser.getSchoolCode());//这里和where的用法一致
```
翻译后的JPQL如下：
```sql
from User user left join user.metas mate on mate.schoolCode = user.schoolCode 
```
# 分组查询
```java
jpaQuery.group(modelUser.getUserName());
```
翻译后的JPQL：
```sql
group by user.userName
```
# Having子句
```java
jpaQuery.having(modelUser.getId()).count().greatThan(20);
```
翻译后的JPQL：
```sql
having count(user.id) > 20
```
# JPQL扩展
在where中加入一段纯JPQL，无限扩展JPQL构造能力
```java
jpaQuery.where().append("and {a} = ? ")//只用大括号和问号来做属性和值占位符
.alias("a",modelCompany.getName())//这里的属性将匹配大括号占位符。
.args("XXX科技股份有限公司");//这里的参数将匹配?号占位符
```
翻译后的JPQL如下：
```sql
... and company.name='XXX科技股份有限公司'
```
**注：select子句也可以类似用法**