This is a copy of [octopus-platform/joern](https://github.com/octopus-platform/joern)

:)THX, and I separate the PHP part from it.

## PHP Joern Lab Version

- [ ] Foreach Sensitivity:
 
  Demo:
  ```php
  <?php
  $arr = $_GET[1]; //source
  foearch($arr as $k => $v)
  {
      echo $k; //sink1
  	  echo $v; //sink2
  }
  ?>
  ```
  Foreach is a very common and useful feature in the PHP codebase. 
  But we don't support this now.
  ```
  start	end	type	var
  5	13	REACHES	arr  //5 the statement of line 2, 13 is the AST_VAR of $arr
  ```
 Current solution methods: Actually, I don't know how to solve this question in a normal way. So I personally move the control flow from the AST_FOREACH's first children AST_VAR(\$arr) to the AST_FOREACH.
 I know that AST_FORACHE is a control node, but if I do this I can observe all of the children(\$arr, \$k, \$v) from the CFG.  Then I can  know the following assignments on the CFG:
   ```
   $k = $arr;
   $v = $arr;
   ```
   If you have a better solution method, please tell me, THX~.

- [X] 6.24: Fix Bug
Issue: No node AST_NULLABLE_TYPE
Fix: Just make it as a null not and ignore it on the cpg.


- [X] Index Sensitivitiy

Demo
```php
<?php
$a['id']['frist'] = $_GET;
$a['id']['second'] = 1; 
echo $a['id']['frist'];
?>
```
To let the data dependency flow from `$a['id']['frist']` point to the `echo`, I change the key on the data flow's key as a_id_frist.
Current solution method: during the use-define link analysis, when the method `reportUpstream()` meet an `ArrayIndexing` enviroment, we obtain its right expression as the index expression and iterator to create a signature of the strings.

- [ ] Field Sensitivity

Demo
```
class A{
    public $id;
    public $username;
}
$user = $_GET['username'];
$obj = new A();
$obj->username = $user;
$obj->id = "123";
echo $obj->username;
```
In this case, we need to let data flow pass `obj->username` from define to use(echo), rather than `obj`.
