#!/bin/bash
#https://www.unix.com/shell-programming-and-scripting/271759-compiling-executing-java-file-shell-script.html
#
cp="${PWD}\target\classes"
p="de.haw.rn.http_client_webserver.client."
red=`tput setaf 1`
green=`tput setaf 2`
reset=`tput sgr0`
#server=$(java -cp "${PWD}\target\classes" ${p}.server.HttpServer -l filelog)

java -cp "${cp}" ${p}HttpClient http://localhost:8080/
echo "${green}Testing GET Method: Test Case 1 - correct output with statuscode 200${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 1 correct Range <From-to>.${reset}"
java -cp "${cp}" ${p}HttpClient -r 0-500 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 2 correct Range <From without to>.${reset}"
java -cp "${cp}" ${p}HttpClient -r 500- http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 3 incorrect Range From > to.${reset}"
java -cp "${cp}" ${p}HttpClient -r 20-5 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 4 incorrect Range From > Total  && without to.${reset}"
java -cp "${cp}" ${p}HttpClient -r 7000- http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 5 incorrect Range From > Total  && with to.${reset}"
java -cp "${cp}" ${p}HttpClient -r 7000-8000 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test: Test case 6 correct Range To> Total Length.${reset}"
java -cp "${cp}" ${p}HttpClient -r 1024-8000 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test HEAD: Test case 1 correct Range <From-to>.${reset}"
curl -X HEAD -r 0-500 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test HEAD: Test case 2 correct Range <From without to>.${reset}"
curl -X HEAD -r 500- http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range tes HEADt: Test case 3 incorrect Range From > to.${reset}"
curl -X HEAD -r 20-5 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test HEAD: Test case 4 incorrect Range From > Total  && without to.${reset}"
curl -X HEAD -r 7000- http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test HEAD: Test case 5 incorrect Range From > Total  && with to.${reset}"
curl -X HEAD -r 7000-8000 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Range test HEAD: Test case 6 correct Range To> Total Length.${reset}"
curl -X HEAD -r 1024-8000 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Directory without content: Test Case 1 List with Content expected${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/graphics/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Directory with absent directory: Test Case 2 404 StatusCode is expected${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/graphics/notofound
echo "--------------------------------------------------------------------\n\n"
echo "${green}Directory with absent file: Test Case 2 404 StatusCode is expected${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/graphics/notofound.html
echo "--------------------------------------------------------------------\n\n"
echo "${green}Invalid http method request: Test Case 1 501 StatusCode is expected${reset}"
curl -X POST http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Emojis are read correctly: Test Case 1${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/text/UTF-8.txt
echo "--------------------------------------------------------------------\n\n"
echo "${green}Parent directory: Test Case 1 accessing parent dir is not allowed, ../ will be deleted automatically${reset}"
java -cp "${cp}" ${p}HttpClient http://localhost:8080/../../
echo "--------------------------------------------------------------------\n\n"
echo "${green}Partial download: Test Case 1 DIFF Command - downloaded file is equal to original${reset}"
curl -r 0-3 -o testPart1.txt http://localhost:8080/text/test.txt
curl -r 3-8 -o testPart2.txt http://localhost:8080/text/test.txt
curl -r 8-12 -o testPart3.txt http://localhost:8080/text/test.txt
curl -r 12- -o testPart4.txt http://localhost:8080/text/test.txt
cat testPart?.txt > testPart.txt
curl -o test.txt http://localhost:8080/text/test.txt
diff testPart.txt test.txt
rm testPart?.txt
rm testPart.txt
rm test.txt
echo "--------------------------------------------------------------------\n\n"
echo "${green}Testing Slow Motion: Test Case 1 - correct parameters + http 200 Status Code expected${reset}"
java -cp "${cp}" ${p}HttpClient -s 4 500 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Testing Slow Motion: Test Case 2 - no parameters${reset}"
java -cp "${cp}" ${p}HttpClient -s http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"
echo "${green}Testing Slow Motion: Test Case 2 - incorrect parameters${reset}"
java -cp "${cp}" ${p}HttpClient -s 1 http://localhost:8080/
echo "--------------------------------------------------------------------\n\n"