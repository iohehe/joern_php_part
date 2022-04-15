This is a copy of [octopus-platform/joern](https://github.com/octopus-platform/joern)

:)THX, and I separate the PHP part from it.





## PHP Joern Lab Version



- [ ] Foreach Issue:

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

  - Current solution way

    Actually, I don't know how to solve this question in a normal way.

    So I personally move the control flow from the AST_FOREACH's first children AST_VAR(\$arr) to the AST_FOREACH.

    I know that AST_FORACHE is a control node, but if I do this I can observe all of the children(\$arr, \$k, \$v) from the CFG.

    Then I can  know the following assignments on the CFG:

    ```
    $k = $arr;
    $v = $arr;
    ```

    If you have a better solution method, please tell me, THX~.

    