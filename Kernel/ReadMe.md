# Devlog:

<div style="font-size:18px;font-family:Calibri">
<h4><u>5/18/2021:</u></h4>

<p>Unfortunately, due to some errors in GitHub commits and memory management the old code containing a lot
of the newest functionality such as conditionals and expression parsing are gone. Instead, we're opting to take this
opportunity to add some adjustments to Compiling and Executing that could make it easier to manage and code.

<u style="color:#87ceeb">Improvements:</u>
<ul><li>Kernel:</li> 
<ul><li>Executor:</li>
<ul><li>In the previous code we Compiled the code blindly and starting parsing right away; any compile time errors occurred here. 
In the Executor class we streamed the instructions and chose which ones to execute by handpicking instructions and skipping ones that shouldn't. 
The best example of this is the way we used to deal with conditionals; we read through the code, if we encountered a conditional we would run it, evaluate it, and skip the block if it was false. 
If it was true, we would copy the block and read through it again. Unfortunately, this was really buggy. 
So I took the time to create a Abstract Syntax Tree class <i>(that honestly acts more like a graph than it does a tree)</i>.
Here's how it works: the compiler will read through the source code and create an instruction file with strict syntax, marking when scopes start and end, and writes what exactly the Executor should do.
This is where a lot of the future implementations for programming ease will be converted into instructions that the Executor class can understand.
Once we do that, we pass off the instruction file to the Executor.
The Executor will create an Abstract Syntax Tree for the file following the procedural path the Compiler laid out for it in the instruction file.
Functions will most likely be directly injected or evaluated here by creating smaller and lighter trees for quick evaluation.
Once the Executor reaches any type of branching like iterations or conditionals it creates nodes onto the Abstract Syntax Tree and injects the block.
That way, the name of the node will be the condition or the iteration to be evaluated, and the information contained within the node is the block of code within it.
Each layer of nodes represents a layer of scope within its parent node. 
Everytime a scope is created, an instruction is written to the parent's code body to evaluate the child node.
</ul>
</ul>
</ul>

<u style="color:#87ceeb">Problems and Possible Solutions:</u>
<ul><li>Abstract Syntax Tree: 
<ul>

<li>Conditionals:
<ul><li>
A problem that we had was how we could evaluate around conditionals and iterations if the evaluation turns out to be false.
First, the solution suggested for the conditionals was mentioned above. 
Everytime we enter a scope we create another node underneath the parent node and include an instruction in the parent node to check it.
If we encounter a conditional that evaluates to false we chop off the entire branch. 
Because we include the code surrounding the conditional in the parent node, and each layer simply adds another scope, we can safely do this.
</li></ul>

<li>Iteration:

<ul><li>
Iteration is an entirely different story. 
A possible solution is that we make one of the nodes containing the iteration block itself.
This way we can continue traversing the graph and evaluating until the condition of the for evaluates to false.
It would also be easy to implement break statements and continue statements; each time we encounter a continue or break we just add an instruction telling the Executor
to check the child containing itself or to return to the parent node respectively.
</li></ul>

<li>Parser:
<ul><li>
The problem with evaluating expressions previously is that we didn't account for numerous types of evaluation in one expression.
With Direct Injection we could inject how we could treat the expression, what RegEx to use, and how we should evaluate it. 
But this didn't work for expressions like:

```
int a = 100;
int b = 200;
bool isGreater = a + b > 1000;
```

The evaluation of "isGreater" would throw an Exception as the parser for the boolean object doesn't know how to treat
number-only operators. The solutions I can think of are messy at best. The idea is to create a Universal Parser class,
which can take in any expression, and evaluate it as normal. The Executor class can swap out the variables for the
scope-wise variables that we use in RVM (next problem) and send the expression to the UniversalParser.
</li></ul>
<li>Memory Manipulation:
<ul><li>
The way memory is represented in the language is through a HashMap. 
Yeah, I know, it can get very memory intensive but it was the fastest way to store and get properties and other information about a variable.
The way that it <i>should</i> work is that everytime the Compiler come's across an instruction that <i>could</i> contain a variable, the Compiler will analyze the scope that it's in (down to the condition) and name it as such.
For example, a local variable in main:

```
$ int main(){
    int a = 100;
    # some code....
}
```

Should be stored as: <code>main.a</code> in the RuntimeVariableManipulation class. If we enter a scope like:

```
$ int main(){
    if(condition a){
        if(condition b){
            int a = 100;
        }
    }   
}
```

Then what should the variable "a" be stored as? This is generally why using a stack is better because we can use the
time a variable was committed to the Stack to our advantage. So the possible alternative, although it would be very
annoying to recode, would be to swap the RuntimeVariableManipulation hashmap for a Stack. This way popping variables off
the stack would be easier. It'd just be more annoying to get variables in higher scopes, and asserting scope dominance
may be a bit harder too if not done correctly. Either way, I'd have to name the variable according to the scope that it'
s in. So, my possible solution to this problem is to use labels. Each time I encounter the "<code>:</code>" character I
know that it must be a label. I can use the name of the label to then name the variable. I have no clue how the syntax
would look. If it isn't specified I could always try and autogenerate a scope name based on when the scope change comes
and where. This is still a massive problem to be fixed.
</li></ul>
</li>
</ul>
</li></ul>
</div>
