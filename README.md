javis
=====
The purpose of Javis is to learn how much of the crawled web application is hidden due to the DOM changes that exist and which search engines do not index and crawl. 

Javis is a crawljax plugin(postcrawlingplugin) which uses the state flow graph of the crawl to categorize each state into either a visible or a hidden state. The edges are also grouped based on which state they conclude to. 

The elements analyzed are Div, Span, Button, Input, A and IMG since they are the most likely clickables that change the DOM tree. However, the anchor tag can interestingly be visible or hidden due to the href value. If the href value is a valid URL then it is assumed it is a visible state as what search engines index. However, on the other hand if no href attribute exists or the href value is not a valid URL, the anchor tag is considered to be a hidden edge. The IMG tag is similar, it can be either a visible clickable or an invisible clickable. To learn the kind of this element, we observe the parent element. If the parent element is a valid anchor tag, then this img element is also considered to be visible and if the parent is an anchor tag but it is a hidden one, or if the parent of the img element is any other element except a visible anchor tag, the img element is considered hidden.
