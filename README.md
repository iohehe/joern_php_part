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

  

  

