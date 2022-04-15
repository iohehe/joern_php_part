<?php
$arr = $_GET[1]; //source

foreach($arr as $k => $v)
{
    echo $k; //sink1
	echo $v; //sink2
}
?>
