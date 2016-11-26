int main()
{
    std::string s("Somewhere down the road");
    std::istringstream iss(s);

    do
    {
        std::string sub;
        iss >> sub;
        std::cout << "Substring: " << sub << std::endl;
    } while (iss);
}